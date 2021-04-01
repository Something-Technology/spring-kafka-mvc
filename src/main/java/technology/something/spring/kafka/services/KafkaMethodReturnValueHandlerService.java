package technology.something.spring.kafka.services;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;

public interface KafkaMethodReturnValueHandlerService extends HandlerMethodReturnValueHandler {
    @Override
    boolean supportsReturnType(MethodParameter returnType);

    @Override
    void handleReturnValue(Object returnValue, MethodParameter returnType, Message<?> message) throws Exception;
}
