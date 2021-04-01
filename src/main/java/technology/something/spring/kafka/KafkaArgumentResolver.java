package technology.something.spring.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.handler.annotation.support.AbstractNamedValueMethodArgumentResolver;

public class KafkaArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    public KafkaArgumentResolver(ConversionService cs, ConfigurableBeanFactory beanFactory) {
        super(cs, beanFactory);
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        if (SpecificRecord.class.isAssignableFrom(parameter.getParameterType())) {
            return new KafkaNamedValueInfo(parameter.getParameterName());
        }
        return null;
    }

    @Override
    protected Object resolveArgumentInternal(MethodParameter parameter, Message<?> message, String name) throws Exception {
        if (!(SpecificRecord.class.isAssignableFrom(parameter.getParameterType()))) {
            return null;
        }
        KafkaRequestData payload = (KafkaRequestData) message.getPayload();
        return payload.getConsumerRecord().value();
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter, Message<?> message) {
        throw new MessageHandlingException(message, "Missing payload parameter '" + name + "' for method parameter type [" + parameter.getParameterType() + "]");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return SpecificRecord.class.isAssignableFrom(parameterType);
    }

    public class KafkaNamedValueInfo extends NamedValueInfo {
        public KafkaNamedValueInfo(String name) {
            super(name, true, null);
        }
    }
}
