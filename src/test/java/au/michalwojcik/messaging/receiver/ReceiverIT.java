package au.michalwojcik.messaging.receiver;

import au.michalwojcik.messaging.LocalStackTestConfiguration;
import au.michalwojcik.messaging.Notification;
import au.michalwojcik.messaging.SimpleNotification;
import au.michalwojcik.messaging.mapper.DeserializationMapper;
import au.michalwojcik.messaging.receiver.resolver.NotificationResolver;
import au.michalwojcik.messaging.receiver.resolver.S3NotificationResolver;
import com.amazonaws.services.s3.event.S3EventNotification;
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
        DeserializationMapper.class,
        Receiver.class,
        SqsAutoConfiguration.class
})
class ReceiverIT {

    @Autowired
    private AmazonSQS amazonSQS;

    @SpyBean
    private NotificationResolver notificationResolver;

    @SpyBean
    private SimpleNotificationHandler simpleNotificationHandler;

    @SpyBean
    private S3NotificationResolver s3NotificationResolver;

    @SpyBean
    private SimpleS3NotificationHandler simpleS3NotificationHandler;

    @Test
    void shouldReceiveNotification() {
        // Given
        String queueUrl = amazonSQS.listQueues().getQueueUrls().get(0);
        // When
        amazonSQS.sendMessage(new SendMessageRequest(queueUrl, SNS_MESSAGE_PAYLOAD));
        amazonSQS.sendMessage(new SendMessageRequest(queueUrl, S3_NOTIFICATION));
        // Then
        Awaitility.await()
                .atMost(Duration.ofSeconds(4))
                .pollInterval(Duration.ofMillis(256))
                .untilAsserted(() -> {
                    Mockito.verify(notificationResolver, Mockito.times(2))
                            .resolve(Mockito.any());
                    Mockito.verify(s3NotificationResolver, Mockito.times(2))
                            .resolve(Mockito.any());

                    Mockito.verify(simpleNotificationHandler, Mockito.times(1))
                            .supports("simple-notification");
                    Mockito.verify(simpleS3NotificationHandler, Mockito.times(1))
                            .supports("path/to/file.csv");

                    Notification<SimpleNotification> notification = new Notification<>(
                            new SimpleNotification("id", LocalDateTime.of(2023, 4, 5, 20, 43)),
                            "simple-notification");
                    Mockito.verify(simpleNotificationHandler, Mockito.times(1))
                            .handle(notification);
                    Mockito.verify(simpleS3NotificationHandler, Mockito.times(1))
                            .handle(Mockito.any(S3EventNotification.S3EventNotificationRecord.class));
                });

        amazonSQS.purgeQueue(new PurgeQueueRequest(queueUrl));
        amazonSQS.deleteQueue(queueUrl);
    }

    private static final String SNS_MESSAGE_PAYLOAD = """
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

    private static final String S3_NOTIFICATION = """
            {
               "Records":[
                  {
                     "eventVersion":"2.2",
                     "eventSource":"aws:s3",
                     "awsRegion":"us-west-2",
                     "eventTime":"1970-01-01T00:00:00.000Z",
                     "eventName":"event-type",
                     "userIdentity":{
                        "principalId":"Amazon-customer-ID-of-the-user-who-caused-the-event"
                     },
                     "requestParameters":{
                        "sourceIPAddress":"ip-address-where-request-came-from"
                     },
                     "responseElements":{
                        "x-amz-request-id":"Amazon S3 generated request ID",
                        "x-amz-id-2":"Amazon S3 host that processed the request"
                     },
                     "s3":{
                        "s3SchemaVersion":"1.0",
                        "configurationId":"ID found in the bucket notification configuration",
                        "bucket":{
                           "name":"bucket-name",
                           "ownerIdentity":{
                              "principalId":"Amazon-customer-ID-of-the-bucket-owner"
                           },
                           "arn":"bucket-ARN"
                        },
                        "object":{
                           "key":"path/to/file.csv",
                           "size": 512,
                           "eTag":"object eTag",
                           "versionId":"object version if bucket is versioning-enabled, otherwise null",
                           "sequencer": "a string representation of a hexadecimal value used to determine event sequence, only used with PUTs and DELETEs"
                        }
                     }
                  }
               ]
            }
            """;
}