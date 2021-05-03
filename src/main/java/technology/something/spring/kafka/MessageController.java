package technology.something.spring.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import technology.something.spring.kafka.services.impl.DefaultKafkaTopicProvider;

public abstract class MessageController {

    @Autowired
    protected DefaultKafkaTopicProvider topicProvider;

}
