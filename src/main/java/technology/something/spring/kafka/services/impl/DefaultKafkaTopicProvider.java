package technology.something.spring.kafka.services.impl;

import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import technology.something.spring.kafka.services.KafkaTopicProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        LOG.info("Register topic {}", topicName);
    }

    @Override
    public String fetchTopicForRecord(Class<? extends SpecificRecord> clazz) {
        String result = map.get(clazz);
        if (result == null) {
            LOG.warn("no registered topic found for {}", clazz.getSimpleName());
        }
        return result;
    }

    @Override
    public Class<? extends SpecificRecord> fetchRecordForTopic(String topic) {
        try {
            Optional foundRecord = map
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(topic))
                .map(Map.Entry::getKey)
                .findFirst();

            if (foundRecord.isPresent()) {
                Class<? extends SpecificRecord> record = (Class<? extends SpecificRecord>) foundRecord.get();
                return record;
            }
        } catch (Exception e) {
            this.handleError(e.getMessage());
        }

        return null;
    }

    private void handleError(String message) {
        LOG.error("ERROR in DefaultKafkaTopicProvider with the following message: {}", message);
    }
}
