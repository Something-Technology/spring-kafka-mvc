package technology.something.spring.kafka.services.impl;

import org.apache.avro.Schema;
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

    @KafkaConsumer
    public void consume(String topic) {
        if (topic.isEmpty()) {
            LOG.error("No topic provided.");
            return;
        }

        if (topicProvider == null) {
            LOG.error("Topic provider not available.");
            return;
        }

        if (topicProvider.fetchTopicForRecord(ExampleController.class) == null) {
            topicProvider.registerTopic(topic, ExampleController.class);
            LOG.info("Registering topic: ", topic);
        }
        LOG.info("Consuming message topic: ", topic);
    }

    public void produceMessage(ExampleMessage message) {
        try {
            producer.handleReturnValue(message, null, null);
        } catch (Exception ex) {
            LOG.error("ERROR: sending of message failed.", ex.getLocalizedMessage());
        }
    }

    @Override
    public void put(int i, Object v) {

    }

    @Override
    public Object get(int i) {
        return null;
    }

    @Override
    public Schema getSchema() {
        return null;
    }
}
