package serializer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaConsumerUser {
    public static void main(String[] args) {
        //创建KafkaConsumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "tcandyj.top:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserDefineDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");

        KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props);
        //订阅topic  通过指定消费分区 ，失去组管理特性
        consumer.subscribe(Arrays.asList("topic02"));
//        consumer.subscribe(Pattern.compile("^topic.*"));
//        List<TopicPartition> partitions = Arrays.asList(new TopicPartition("topic02", 0));
//        consumer.assign(partitions);

        //指定消费分区的位置
//        consumer.seekToBeginning(partitions);

        //设置消费分区指定位置
//        consumer.seek(new TopicPartition("topic01", 0),0);

        //遍历消息队列
        while (true) {
            ConsumerRecords<String, User> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            if (!consumerRecords.isEmpty()) {
                Iterator<ConsumerRecord<String, User>> iterator = consumerRecords.iterator();
                while (iterator.hasNext()) {
                    //获取一个消费消息
                    ConsumerRecord<String, User> record = iterator.next();
                    String topic = record.topic();
                    int partition = record.partition();
                    long offset = record.offset();

                    String key = record.key();
                    User value = record.value();

                    System.out.println(topic + "\t" + partition + "\t" + offset + "\t" + key + "\t" + value.toString());
                }
            }
        }
    }
}
