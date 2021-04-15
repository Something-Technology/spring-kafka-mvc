package technology.something.spring.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import technology.something.spring.kafka.services.impl.DefaultKafkaConsumerService;
import technology.something.spring.kafka.services.impl.DefaultKafkaMethodReturnValueHandlerService;

import static org.mockito.Mockito.mock;

@Profile("test")
@Configuration
public class MockApplicationConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(MockApplicationConfiguration.class);

    @Bean
    @Primary
    public DefaultKafkaConsumerService kafkaConsumerService() { return mock(DefaultKafkaConsumerService.class); }

}

