package technology.something.spring.kafka.services;

import org.apache.avro.specific.SpecificRecord;

public interface KafkaTopicProvider {

    public void registerTopic(String topicName, Class<? extends SpecificRecord> clazz);

    public String fetchTopicForRecord(Class<? extends SpecificRecord> clazz);

    public Class<? extends SpecificRecord> fetchRecordForTopic(String topic);

}
