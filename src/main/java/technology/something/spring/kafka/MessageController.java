package technology.something.spring.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import technology.something.spring.kafka.services.impl.DefaultKafkaMessageHandlerService;
import technology.something.spring.kafka.services.impl.DefaultKafkaMethodReturnValueHandlerService;
import technology.something.spring.kafka.services.impl.DefaultKafkaTopicProvider;

public abstract class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    protected DefaultKafkaTopicProvider topicProvider;

    @Autowired
    protected DefaultKafkaMessageHandlerService messageHandlerService;

    @Autowired
    protected DefaultKafkaMethodReturnValueHandlerService methodReturnValueHandlerService;

    protected boolean isTopicProviderAvailable() {
        if (topicProvider == null) {
            LOG.error("Topic provider not available.");
            return false;
        }
        return true;
    }
}
