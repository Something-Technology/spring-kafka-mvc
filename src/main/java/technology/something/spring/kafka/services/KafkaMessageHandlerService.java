package technology.something.spring.kafka.services;

import technology.something.spring.kafka.KafkaMessageInfo;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public interface KafkaMessageHandlerService extends MessageHandler {
    boolean isHandler(Class<?> beanType);

    String getDestination(Message<?> message);

    Set<String> getDirectLookupDestinations(KafkaMessageInfo mapping);

    KafkaMessageInfo getMappingForMethod(Method method, Class<?> handlerType);

    List<? extends HandlerMethodArgumentResolver> initArgumentResolvers();

    List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers();

    AbstractExceptionHandlerMethodResolver createExceptionHandlerMethodResolverFor(Class<?> beanType);

    Comparator<KafkaMessageInfo> getMappingComparator(Message<?> message);

    KafkaMessageInfo getMatchingMapping(KafkaMessageInfo mapping, Message<?> message);
}
