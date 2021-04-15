package technology.something.spring.kafka.services.impl;

import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import technology.something.spring.kafka.services.KafkaTopicProvider;

import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultKafkaTopicProvider implements KafkaTopicProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultKafkaTopicProvider.class);

    /**
     * Map defined by specified Record class and topic name
     */
    private final Map<Class<? extends SpecificRecord>, String> map = new HashMap<>();

    @Override
    public void registerTopic(String topicName, Class<? extends SpecificRecord> clazz) {
        map.put(clazz, topicName);
    }

    @Override
    public String fetchTopicForRecord(Class<? extends SpecificRecord> clazz) {
        String result = map.get(clazz);
        if (result == null) {
            LOG.warn("no registered topic found for " + clazz.getName());
        }
        return result;
    }
}
