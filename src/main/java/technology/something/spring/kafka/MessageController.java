package technology.something.spring.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Autowired;
import technology.something.spring.kafka.services.impl.DefaultKafkaTopicProvider;

public abstract class MessageController implements SpecificRecord {

    @Autowired
    protected DefaultKafkaTopicProvider topicProvider;

}
