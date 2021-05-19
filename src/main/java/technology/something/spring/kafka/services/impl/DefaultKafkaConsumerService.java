package technology.something.spring.kafka.services.impl;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import technology.something.spring.kafka.KafkaConsumer;
import technology.something.spring.kafka.KafkaMessageInfo;
import technology.something.spring.kafka.KafkaRequestData;
import technology.something.spring.kafka.services.KafkaConsumerService;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Kafka consumer service should be run from the microservice where this mvc project has been implemented.
 */
@Service
public class DefaultKafkaConsumerService implements KafkaConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultKafkaConsumerService.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DefaultKafkaMessageHandlerService kafkaMessageHandler;

    @Autowired
    private ConsumerFactory<String, GenericRecord> consumerFactory;

    @Autowired
    private DefaultKafkaTopicProvider topicProvider;

    @PostConstruct
    public void postConstruct() {
        Set<String> allConsumedTopics = new HashSet<>();
        applicationContext.getBeansWithAnnotation(Controller.class).forEach((key, object) -> {
            List<Method> list = Arrays.asList(object.getClass().getMethods());
            Set foundTopics = list.stream()
                .filter(method -> method.isAnnotationPresent(KafkaConsumer.class))
                .map(this::extractTopicName)
                .filter(topicName -> topicName != null)
                .collect(Collectors.toSet());
            allConsumedTopics.addAll(foundTopics);
        });

        if (!allConsumedTopics.isEmpty()) {
            ContainerProperties containerProperties = new ContainerProperties(allConsumedTopics.toArray(new String[allConsumedTopics.size()]));
            containerProperties.setMessageListener((MessageListener<String, GenericRecord>) record -> {
                KafkaRequestData requestData = new KafkaRequestData(record);
                Class<? extends SpecificRecord> specificRecord = topicProvider.fetchRecordForTopic(record.topic());
                Message<KafkaRequestData> message = MessageBuilder
                    .withPayload(requestData)
                    .setHeader(KafkaMessageInfo.HEADER_TYPE, specificRecord.getName())
                    .build();
                kafkaMessageHandler.setArgumentResolvers(Collections.singletonList(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter methodParameter) {
                        return methodParameter.getParameterType().equals(Message.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
                        return message;
                    }
                }));
                kafkaMessageHandler.handleMessage(message);
            });
            KafkaMessageListenerContainer<String, GenericRecord> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
            container.start();
        }
    }

    private String extractTopicName(Method method) {
        KafkaConsumer consumer = method.getAnnotation(KafkaConsumer.class);
        if (consumer == null) {
            LOG.info("Method has no Consumer annotation.");
            return null;
        }

        Class<?> parameterType = consumer.value();
        if (SpecificRecord.class.isAssignableFrom(parameterType)) {
            return topicProvider.fetchTopicForRecord((Class<? extends SpecificRecord>) parameterType);
        }

        return null;
    }
}
