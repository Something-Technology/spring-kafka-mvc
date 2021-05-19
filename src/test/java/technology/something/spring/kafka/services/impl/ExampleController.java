package technology.something.spring.kafka.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import technology.something.spring.kafka.KafkaConsumer;
import technology.something.spring.kafka.MessageController;

@Controller
public class ExampleController extends MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleController.class);

    @Autowired
    private DefaultKafkaMethodReturnValueHandlerService producer;

    @KafkaConsumer(ExampleMessage.class)
    public void consume() {

        if (topicProvider == null) {
            LOG.error("Topic provider not available.");
            return;
        }

        if (topicProvider.fetchTopicForRecord(ExampleMessage.class) == null) {
            topicProvider.registerTopic(ExampleMessage.TOPIC, ExampleMessage.class);
            LOG.info("Registering topic: {}", ExampleMessage.TOPIC);
        }
        LOG.info("Consuming message topic: {}", ExampleMessage.TOPIC);
    }

    public void produceMessage(ExampleResponseMessage message) {
        try {
            producer.handleReturnValue(message, null, null);
        } catch (Exception ex) {
            LOG.error("ERROR: sending of message failed. {}", ex.getLocalizedMessage());
        }
    }

}
