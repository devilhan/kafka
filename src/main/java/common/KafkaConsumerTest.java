package common;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaConsumerTest {
    public static void main(String[] args) {
        //创建KafkaConsumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "tcandyj.top:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        //订阅topic
        consumer.subscribe(Pattern.compile("^topic.*"));
        //遍历消息队列
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            if (!consumerRecords.isEmpty()) {
                Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
                while (iterator.hasNext()) {
                    //获取一个消费消息
                    ConsumerRecord<String, String> record = iterator.next();
                    String topic = record.topic();
                    int partition = record.partition();
                    long offset = record.offset();

                    String key = record.key();
                    String value = record.value();

                    System.out.println(topic + "\t" + partition + "\t" + offset + "\t" + key + "\t" + value);
                }
            }
        }
    }
}
