package interceptor;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaProducerInterceptor {

    public static void main(String[] args) {

        //创建Kafka Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"tcandyj.top:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, UserDefineProducerInterceptor.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        for (int i=0;i<10;i++){
            /*
                producer 如果有key : 将采用 hash 方式
                         如果没key : 将采用 轮询 方式
             */
            ProducerRecord<String, String> record = new ProducerRecord<>("topic04",  "key" + i, "value" + i);
            //发送给服务器
            producer.send(record);
        }

        producer.close();
    }
}
