package serializer;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @Author: devilhan
 * @Date: 2021/6/28
 * @Description:
 */
public class UserDefineDeserializer implements Deserializer {
    @Override
    public void configure(Map map, boolean b) {
        System.out.println("configure");
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        return SerializationUtils.deserialize(data);
    }

    @Override
    public void close() {
        System.out.println("close");
    }
}
