package transaction;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaProducerOnlyTransaction {

    public static void main(String[] args) {

        KafkaProducer<String, String> producer = buildKafkaProducer();

        //初始化事务
        producer.initTransactions();
        try {
            producer.beginTransaction();
            for (int i = 0; i < 30; i++) {
                /*if (i == 20) {
                    int j = i / 0;
                }*/
                ProducerRecord<String, String> record = new ProducerRecord<>("topic02", 0, "transaction" + i, "right value" + i);
                //发送给服务器
                producer.send(record);
                producer.flush();
            }
            producer.commitTransaction();
        } catch (Exception e) {
            System.out.println("error：" + e.getMessage());
            producer.abortTransaction();
        } finally {
            producer.close();
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
}
