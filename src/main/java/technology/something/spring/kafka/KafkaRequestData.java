package technology.something.spring.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * This class holds consumed request data for further usage.
 */
public class KafkaRequestData {

    private final ConsumerRecord<String, ?> consumerRecord;

    public KafkaRequestData(ConsumerRecord<String, ?> consumer) {
        this.consumerRecord = consumer;
    }

    public ConsumerRecord<String, ?> getConsumerRecord() {
        return consumerRecord;
    }
}
