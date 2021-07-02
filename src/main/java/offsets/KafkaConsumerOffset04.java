package offsets;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import serializer.User;
import serializer.UserDefineDeserializer;

import java.time.Duration;
import java.util.*;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaConsumerOffset04 {
    public static void main(String[] args) {
        //创建KafkaConsumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "tcandyj.top:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserDefineDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group4");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //配置offset自动提交的时间间隔
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

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
                //记录分区消费元数据信息
                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<TopicPartition, OffsetAndMetadata>();

                while (iterator.hasNext()) {
                    //获取一个消费消息
                    ConsumerRecord<String, User> record = iterator.next();
                    String topic = record.topic();
                    int partition = record.partition();
                    long offset = record.offset();

                    String key = record.key();
                    User value = record.value();

                    //记录消费分区的偏移量元数据
                    offsets.put(new TopicPartition(topic, partition), new OffsetAndMetadata(offset + 1));

                    System.out.println(topic + "\t" + partition + "\t" + offset + "\t" + key + "\t" + value.toString());

                    consumer.commitAsync(offsets, new OffsetCommitCallback() {
                        @Override
                        public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                            System.out.println("offsets:" + offsets + "exception:" + e);
                        }
                    });
                }
            }
        }
    }
}
