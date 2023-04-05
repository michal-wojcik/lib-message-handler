package au.michalwojcik.messaging.sender;

import au.michalwojcik.messaging.LocalStackTestConfiguration;
import au.michalwojcik.messaging.SimpleMessage;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import io.awspring.cloud.autoconfigure.messaging.SnsAutoConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author michal-wojcik
 */
@Import(LocalStackTestConfiguration.class)
@TestPropertySource(properties = "message.sender.topic.name=topic-name")
@SpringBootTest(classes = {
        Sender.class,
        SnsAutoConfiguration.class
})
class SenderIntegrationTest {

    @Autowired
    private Sender sender;

    @Autowired
    private AmazonSNSAsync amazonSNSAsync;

    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    @Test
    void shouldSendMessage() {
        // Given
        String topicArn = amazonSNSAsync.createTopic("topic-name").getTopicArn();
        String queueUrl = amazonSQSAsync.createQueue("queue-name").getQueueUrl();

        String subscriptionArn = Topics.subscribeQueue(amazonSNSAsync, amazonSQSAsync, topicArn, queueUrl);

        SimpleMessage simpleMessage = new SimpleMessage(
                "id",
                LocalDateTime.of(2023, 4, 5, 20, 43));
        // When
        sender.send(simpleMessage, "simple-message");
        // Then
        Awaitility.await()
                .atMost(Duration.ofSeconds(4))
                .pollInterval(Duration.ofMillis(256))
                .untilAsserted(() -> {
                    Assertions.assertNotNull(subscriptionArn);

                    ReceiveMessageResult receiveMessageResult = amazonSQSAsync.receiveMessage(queueUrl);
                    Assertions.assertFalse(receiveMessageResult.getMessages().isEmpty());
                });
    }
}