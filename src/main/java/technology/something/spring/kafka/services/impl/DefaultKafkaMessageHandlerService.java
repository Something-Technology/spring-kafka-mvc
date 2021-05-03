package technology.something.spring.kafka.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.AnnotationExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import technology.something.spring.kafka.KafkaArgumentResolver;
import technology.something.spring.kafka.KafkaConsumer;
import technology.something.spring.kafka.KafkaMessageInfo;
import technology.something.spring.kafka.services.KafkaMessageHandlerService;
import technology.something.spring.kafka.services.KafkaMethodReturnValueHandlerService;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Kafka message handler is responsible for the mapping of the Kafka messages.
 * The Kafka message will be read and mapped to the right type
 */

@Service
public class DefaultKafkaMessageHandlerService extends AbstractMethodMessageHandler<KafkaMessageInfo>implements KafkaMessageHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaMessageHandlerService.class);

    @Autowired
    private KafkaMethodReturnValueHandlerService returnValueHandler;

    @Override public boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
    }

    @Override public String getDestination(Message<?> message) {
        return message.getHeaders().get(KafkaMessageInfo.HEADER_TYPE, String.class);
    }

    @Override public Set<String> getDirectLookupDestinations(KafkaMessageInfo mapping) {
        return Collections.emptySet();
    }

    @Override public KafkaMessageInfo getMappingForMethod(Method method, Class<?> handlerType) {
        KafkaConsumer consumer = method.getAnnotation(KafkaConsumer.class);
        if (consumer != null) {
            return new KafkaMessageInfo(consumer.value().getSimpleName());
        }
        return null;
    }

    @Override public List<? extends HandlerMethodArgumentResolver> initArgumentResolvers() {
        ConfigurableBeanFactory beanFactory = (getApplicationContext() instanceof ConfigurableApplicationContext ?
                ((ConfigurableApplicationContext) getApplicationContext()).getBeanFactory() : null);
        List<HandlerMethodArgumentResolver> resolverList = new ArrayList<>();
        resolverList.add(new KafkaArgumentResolver(new DefaultConversionService(), beanFactory));
        return resolverList;
    }

    @Override public List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlerList = new ArrayList<>();
        handlerList.add(returnValueHandler);
        return handlerList;
    }

    @Override public AbstractExceptionHandlerMethodResolver createExceptionHandlerMethodResolverFor(Class<?> beanType) {
        return new AnnotationExceptionHandlerMethodResolver(beanType);
    }

    @Override public Comparator<KafkaMessageInfo> getMappingComparator(Message<?> message) {
        return new Comparator<KafkaMessageInfo>() {
            @Override
            public int compare(KafkaMessageInfo o1, KafkaMessageInfo o2) {
                return o1.getType().compareTo(o2.getType());
            }
        };
    }

    @Override public KafkaMessageInfo getMatchingMapping(KafkaMessageInfo mapping, Message<?> message) {
        String kafkaType = message.getHeaders().get(KafkaMessageInfo.HEADER_TYPE, String.class);
        if (kafkaType.equals(mapping.getType())) {
            return new KafkaMessageInfo(kafkaType);
        }

        return null;
    }

}
