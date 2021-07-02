package partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: devilhan
 * @Date: 2021/6/25
 * @Description:
 */
public class UserDefinePartition implements Partitioner {

    AtomicInteger counter = new AtomicInteger(0);

    /**
     *
     * @param topic
     * @param key
     * @param keyBytes
     * @param value
     * @param valueBytes
     * @param cluster
     * @return 分区号
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();

        if(keyBytes == null){  //轮询
            int andIncrement = counter.getAndIncrement();
            return (andIncrement & Integer.MAX_VALUE) % numPartitions;
        }else{
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }

    @Override
    public void close() {
        System.out.println("close");
    }

    @Override
    public void configure(Map<String, ?> map) {
        System.out.println("configure");
    }
}
