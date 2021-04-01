package technology.something.spring.kafka.services.impl;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import technology.something.spring.kafka.KafkaConsumer;
import technology.something.spring.kafka.KafkaMessageInfo;
import technology.something.spring.kafka.KafkaRequestData;
import technology.something.spring.kafka.services.KafkaConsumerService;
import technology.something.spring.kafka.services.KafkaMessageHandlerService;
import technology.something.spring.kafka.services.KafkaTopicProvider;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultKafkaConsumerService implements KafkaConsumerService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private KafkaMessageHandlerService kafkaMessageHandlerService;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Autowired
    private KafkaTopicProvider topicProvider;

    @PostConstruct
    public void postConstruct() {
        Set<String> allConsumedTopics = new HashSet<>();
        applicationContext.getBeansWithAnnotation(Controller.class).forEach((key, object) -> {
            List<Method> list = Arrays.asList(object.getClass().getMethods());
            Set foundTopics = list.stream().filter(method -> method.isAnnotationPresent(KafkaConsumer.class))
                    .map(this::extractTopicName)
                    .filter(topicName -> topicName != null)
                    .collect(Collectors.toSet());
            allConsumedTopics.addAll(foundTopics);
        });
        ContainerProperties containerProperties = new ContainerProperties(allConsumedTopics.toArray(new String[allConsumedTopics.size()]));
        containerProperties.setMessageListener((MessageListener<String, SpecificRecord>) record -> {
            KafkaRequestData requestData = new KafkaRequestData(record);
            Message<KafkaRequestData> message = MessageBuilder.withPayload(requestData).setHeader(KafkaMessageInfo.HEADER_TYPE, record.value().getClass().getName()).build();
            kafkaMessageHandlerService.handleMessage(message);
        });
        KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.start();
    }

    private String extractTopicName(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return null;
        }
        Class<?> parameterType = parameters[0].getType();
        if (SpecificRecord.class.isAssignableFrom(parameterType)) {
            return topicProvider.fetchTopicForRecord((Class<? extends SpecificRecord>) parameterType);
        }

        return null;
    }
}
