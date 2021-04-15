package technology.something.spring.kafka.services.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import technology.something.spring.kafka.MockApplication;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockApplication.class)
class ConsumerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DefaultKafkaConsumerService service;

    @Autowired
    private MessageController controller;

    @Autowired
    private DefaultKafkaTopicProvider topicProvider;

    @Test
    void consumeTopicWillFindRecord() {
        String topic = "COLLECT_COORDINATES";
        controller.consume(topic);

        Assert.assertEquals(topic, topicProvider.fetchTopicForRecord(MessageController.class));
    }

}
