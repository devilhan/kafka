package transaction;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.*;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaProducerAndConsumerTransaction {

    public static void main(String[] args) {

        KafkaProducer<String, String> producer = buildKafkaProducer();
        KafkaConsumer<String, String> consumer = buildKafkaConsumer("group1");
        //初始化事务
        producer.initTransactions();
        consumer.subscribe(Arrays.asList("topic02"));

        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            if (!consumerRecords.isEmpty()) {
                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                Iterator<ConsumerRecord<String, String>> recordIterator = consumerRecords.iterator();
                producer.beginTransaction();
                try {
                    while (recordIterator.hasNext()){
                        ConsumerRecord<String,String> record = recordIterator.next();
                        offsets.put(new TopicPartition(record.topic(),record.partition()),new OffsetAndMetadata(record.offset()+1));
                        ProducerRecord<String,String> pRecord = new ProducerRecord<>("topic03",record.key(),record.value()+"devilhan");
                        producer.send(pRecord);
                    }

                    //提交消费者的偏移量
                    producer.sendOffsetsToTransaction(offsets,"group1");
                    producer.commitTransaction();
                }catch (Exception e){
                    System.out.println("error：" + e.getMessage());
                    producer.abortTransaction();
                }finally {
                    producer.close();
                }
            }
        }
    }

    public static KafkaProducer<String, String> buildKafkaProducer() {
        //创建Kafka Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "tcandyj.top:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //必须配置事务ID。必须是唯一的ID
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction-id" + UUID.randomUUID().toString());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 1024);
        //如果batch中的数据不足1024大小，则等待5毫秒
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        //配置Kafka 的重试机制和幂等性
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 20000);

        return new KafkaProducer<>(props);
    }

    public static KafkaConsumer<String, String> buildKafkaConsumer(String groupId) {
        //创建KafkaConsumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "tcandyj.top:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        //设置消费者的消费事务隔离级别：read_commit
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        //必须关闭消费者端的offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(props);
    }
}
