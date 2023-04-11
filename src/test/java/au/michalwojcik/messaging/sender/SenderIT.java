package au.michalwojcik.messaging.sender;

import au.michalwojcik.messaging.LocalStackTestConfiguration;
import au.michalwojcik.messaging.SimpleNotification;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
@TestPropertySource(properties = "notification.sender.topic.name=topic-name")
@SpringBootTest(classes = {
        Sender.class,
        SnsAutoConfiguration.class
})
class SenderIT {

    @Autowired
    private Sender sender;

    @Autowired
    private AmazonSNSAsync amazonSNS;

    @Autowired
    private AmazonSQSAsync amazonSQS;

    @Test
    void shouldSendNotification() {
        // Given
        String topicArn = amazonSNS.listTopics().getTopics().get(0).getTopicArn();
        String queueUrl = amazonSQS.listQueues().getQueueUrls().get(0);

        String subscriptionArn = Topics.subscribeQueue(amazonSNS, amazonSQS, topicArn, queueUrl);

        SimpleNotification simpleNotification = new SimpleNotification(
                "id",
                LocalDateTime.of(2023, 4, 5, 20, 43));
        // When
        sender.send(simpleNotification, "simple-notification");
        // Then
        Awaitility.await()
                .atMost(Duration.ofSeconds(4))
                .pollInterval(Duration.ofMillis(256))
                .untilAsserted(() -> {
                    Assertions.assertNotNull(subscriptionArn);

                    ReceiveMessageResult receiveMessageResult = amazonSQS.receiveMessage(queueUrl);
                    Assertions.assertFalse(receiveMessageResult.getMessages().isEmpty());

                    JsonNode body = JsonMapper.builder().build().readTree(receiveMessageResult.getMessages().get(0).getBody());
                    Assertions.assertEquals(
                            "{\"event\":{\"id\":\"id\",\"timestamp\":[2023,4,5,20,43]},\"eventName\":\"simple-notification\"}",
                            body.get("Message").asText()
                    );
                });

        amazonSQS.purgeQueue(new PurgeQueueRequest(queueUrl));
    }
}