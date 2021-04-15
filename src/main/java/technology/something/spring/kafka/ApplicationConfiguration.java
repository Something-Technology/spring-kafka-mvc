package technology.something.spring.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.codehaus.jackson.map.ser.std.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> map = new HashMap<>();
        kafkaProperties.getProperties().forEach((key, value) -> map.put(key, value));
        map.put("key.deserializer", KafkaAvroDeserializer.class.getName());
        map.put("value.deserializer", KafkaAvroDeserializer.class.getName());
        map.put("group.id", "consumer");
        return new DefaultKafkaConsumerFactory<>(map);
    }
    @Bean
    public ProducerFactory<String, SpecificRecord> producerFactory() {
        Map<String, Object> map = new HashMap<>();
        kafkaProperties.getProperties().forEach((key, value) -> map.put(key, value));
        map.put("key.serializer", StringSerializer.class.getName());
        map.put("value.serializer", KafkaAvroSerializer.class.getName());
//        map.put("value.subject.name.strategy", RecordNameStrategy.class.getName());
        map.put("group.id", "producer");
        return new DefaultKafkaProducerFactory<>(map);
    }
    @Bean
    public KafkaTemplate<String, SpecificRecord> sendingTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
