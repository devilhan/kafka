package idempotent;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import serializer.User;
import serializer.UserDefineSerializer;

import java.util.Date;
import java.util.Properties;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaProducerIdempotent {

    public static void main(String[] args) {

        //创建Kafka Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "tcandyj.top:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserDefineSerializer.class.getName());

        //设置kafka acks and retries
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        //如果系统重新发送3次失败，则放弃发送
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        //将检测超市的时间设置为1ms
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1);

        //开启幂等性
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //设置消息严格有序
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,1);

        KafkaProducer<String, User> producer = new KafkaProducer<>(props);

        /*
            producer 如果有key : 将采用 hash 方式
                     如果没key : 将采用 轮询 方式
         */
        ProducerRecord<String, User> record = new ProducerRecord<String, User>("topic02", "idempotent", new User(1, "user", new Date()));
        //发送给服务器
        producer.send(record);
        producer.flush();
        producer.close();
    }
}
