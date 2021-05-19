package technology.something.spring.kafka.services.impl;

import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import technology.something.spring.kafka.services.KafkaMethodReturnValueHandlerService;
import technology.something.spring.kafka.services.KafkaTopicProvider;

import java.lang.reflect.Array;
import java.util.Collection;

@Service
public class DefaultKafkaMethodReturnValueHandlerService implements KafkaMethodReturnValueHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultKafkaMethodReturnValueHandlerService.class);

    @Autowired
    private KafkaTemplate<String, SpecificRecord> sendingTemplate;

    @Autowired
    private KafkaTopicProvider topicProvider;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, Message<?> message) throws Exception {
        if (returnValue == null) {
            LOG.debug("message got no return value. discarding");
            return;
        }

        testAndSendObject(returnValue);
    }

    private void testAndSendObject(Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof SpecificRecord) {
            testAndSendSingleObject(object);
        } else if (object instanceof Collection) {
            for (Object item : (Collection<?>) object) {
                testAndSendSingleObject(item);
            }
        } else if (object.getClass().isArray()) {
            for (int counter = 0; counter < Array.getLength(object); ++counter) {
                testAndSendSingleObject(Array.get(object, counter));
            }
        }
    }

    private void testAndSendSingleObject(Object object) {
        if (!(object instanceof SpecificRecord)) {
            return;
        }

        SpecificRecord specificRecord = (SpecificRecord) object;
        String topicName = topicProvider.fetchTopicForRecord(specificRecord.getClass());
        if (topicName != null) {
            sendingTemplate.send(topicName, specificRecord);
        }
    }

}
