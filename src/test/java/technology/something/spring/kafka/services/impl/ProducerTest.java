package technology.something.spring.kafka.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import technology.something.spring.kafka.MockApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockApplication.class)
public class ProducerTest {

    @InjectMocks
    private MessageController controller;

    @Mock
    private DefaultKafkaMethodReturnValueHandlerService producerService;

    @Mock
    private Logger log;

    @Test
    void produceMessageWithGivenMessageSendsMessage() throws Exception {
        ExampleMessage message = new ExampleMessage();

        doNothing().when(producerService).handleReturnValue(any(), any(), any());

        controller.produceMessage(message);

        verify(producerService).handleReturnValue(message, null, null);
    }

    @Test
    void produceMessageWithNoMessageWontSendsMessage() throws Exception {
        doCallRealMethod().when(producerService).handleReturnValue(any(), any(), any());

        controller.produceMessage(null);

        verify(producerService).handleReturnValue(null, null, null);
    }
}
