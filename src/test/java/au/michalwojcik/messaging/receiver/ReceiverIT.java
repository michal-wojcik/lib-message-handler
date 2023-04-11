package au.michalwojcik.messaging.receiver;

import au.michalwojcik.messaging.LocalStackTestConfiguration;
import au.michalwojcik.messaging.Notification;
import au.michalwojcik.messaging.SimpleNotification;
import au.michalwojcik.messaging.receiver.handler.NotificationHandler;
import au.michalwojcik.messaging.receiver.mapper.MessageMapper;
import au.michalwojcik.messaging.receiver.resolver.NotificationResolver;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import io.awspring.cloud.autoconfigure.messaging.SqsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author michal-wojcik
 */
@Import(LocalStackTestConfiguration.class)
@TestPropertySource(properties = "notification.receiver.queue.name=queue-name")
@SpringBootTest(classes = {
        Receiver.class,
        MessageMapper.class,
        SqsAutoConfiguration.class
})
class ReceiverIT {

    @Autowired
    private AmazonSQS amazonSQS;

    @SpyBean
    private NotificationResolver notificationResolver;

    @SpyBean
    private SimpleNotificationHandler simpleNotificationHandler;

    @Test
    void shouldReceiveNotification() {
        // Given
        String queueUrl = amazonSQS.listQueues().getQueueUrls().get(0);
        // When
        amazonSQS.sendMessage(new SendMessageRequest(queueUrl, SNS_PAYLOAD));
        // Then
        Awaitility.await()
                .atMost(Duration.ofSeconds(4))
                .pollInterval(Duration.ofMillis(256))
                .untilAsserted(() -> {
                    Mockito.verify(notificationResolver, Mockito.times(1))
                            .resolve(Mockito.any());

                    Mockito.verify(simpleNotificationHandler, Mockito.times(1))
                            .supports("simple-notification");

                    Notification<SimpleNotification> notification = new Notification<>(
                            new SimpleNotification("id", LocalDateTime.of(2023, 4, 5, 20, 43)),
                            "simple-notification");
                    Mockito.verify(simpleNotificationHandler, Mockito.times(1))
                            .handle(notification);
                });

        amazonSQS.purgeQueue(new PurgeQueueRequest(queueUrl));
    }

    private static final class SimpleNotificationHandler implements NotificationHandler<Notification<SimpleNotification>> {

        @Override
        public void handle(Notification<SimpleNotification> simpleNotificationNotification) {

        }

        @Override
        public boolean supports(String argument) {
            return "simple-notification".equals(argument);
        }

        @Override
        public Class<?> getType() {
            return SimpleNotification.class;
        }
    }

    private static final String SNS_PAYLOAD = """
            {
              "Type": "Notification",
              "MessageId": "9b244a0f-6334-499b-93bd-cb171aadc967",
              "TopicArn": "arn:aws:sns:us-east-1:000000000000:topic-name",
              "Message": "{\\"event\\":{\\"id\\":\\"id\\",\\"timestamp\\":[2023,4,5,20,43]},\\"eventName\\":\\"simple-notification\\"}",
              "Timestamp": "2023-04-11T00:03:20.459Z",
              "SignatureVersion": "1",
              "Signature": "EXAMPLEpH+..",
              "SigningCertURL": "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-0000000000000000000000.pem",
              "UnsubscribeURL": "http://localhost:4566/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-1:000000000000:topic-name:6ba1533b-be63-446b-9237-d9a2826b500c",
              "MessageAttributes": {
                "id": {
                  "Type": "String",
                  "Value": "4e224cc9-793a-9a8e-f039-31915b324636"
                },
                "contentType": {
                  "Type": "String",
                  "Value": "application/json"
                },
                "timestamp": {
                  "Type": "Number.java.lang.Long",
                  "Value": "1681171400457"
                }
              }
            }
            """;
}