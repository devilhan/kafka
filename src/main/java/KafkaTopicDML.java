import org.apache.kafka.clients.admin.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @Author: devilhan
 * @Date: 2021/6/24
 * @Description:
 */
public class KafkaTopicDML {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //创建kafkaAdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "tcandyj.top:9092");
        KafkaAdminClient adminClient = (KafkaAdminClient) KafkaAdminClient.create(props);

        //创建Topic
        /*CreateTopicsResult createTopics = adminClient.createTopics(Arrays.asList(new NewTopic("topic02", 2, (short) 1)));
        createTopics.all().get();*/  //同步创建

        //查看Topic列表
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> set = topicsResult.names().get();
        for (String str : set) {
            System.out.println(str);
        }

        //删除Topic
        /*DeleteTopicsResult deleteTopics = adminClient.deleteTopics(Arrays.asList("topic02"));
        deleteTopics.all().get(); */  //同步删除

        //Topic详情
       /* DescribeTopicsResult describeTopics = adminClient.describeTopics(Arrays.asList("topic01","topic02"));
        Map<String, TopicDescription> descriptionMap = describeTopics.all().get();
        for (Map.Entry<String, TopicDescription> entry : descriptionMap.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }*/

        adminClient.close();
    }
}
