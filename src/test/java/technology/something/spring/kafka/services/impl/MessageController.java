package technology.something.spring.kafka.services.impl;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import technology.something.spring.kafka.KafkaConsumer;

@Controller
public class MessageController implements SpecificRecord {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private DefaultKafkaTopicProvider topicProvider;

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

        if (topicProvider.fetchTopicForRecord(MessageController.class) == null) {
            topicProvider.registerTopic(topic, MessageController.class);
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
