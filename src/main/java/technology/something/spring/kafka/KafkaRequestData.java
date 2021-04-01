package technology.something.spring.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaRequestData {

    private final ConsumerRecord<String, ?> consumerRecord;

    public KafkaRequestData(ConsumerRecord<String, ?> consumer) {
        this.consumerRecord = consumer;
    }

    public ConsumerRecord<String, ?> getConsumerRecord() {
        return consumerRecord;
    }
}
