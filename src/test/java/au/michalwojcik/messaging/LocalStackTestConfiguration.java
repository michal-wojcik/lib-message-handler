package au.michalwojcik.messaging;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * @author michal-wojcik
 */
@Testcontainers(disabledWithoutDocker = true)
public class LocalStackTestConfiguration {

    private static final AWSStaticCredentialsProvider AWS_CREDENTIALS_PROVIDER = new AWSStaticCredentialsProvider(
            new BasicAWSCredentials("noop", "noop"));

    @Container
    private static final LocalStackContainer LOCAL_STACK_CONTAINER = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:1.3.1"))
            .withServices(LocalStackContainer.Service.SQS)
            .withServices(LocalStackContainer.Service.SNS)
            .withReuse(true);

    @Lazy
    @Bean
    AmazonSQSAsync amazonSQS() {
        startContainer();

        AmazonSQSAsync amazonSQS = new AmazonSQSBufferedAsyncClient(
                AmazonSQSAsyncClient
                        .asyncBuilder()
                        .withCredentials(AWS_CREDENTIALS_PROVIDER)
                        .withEndpointConfiguration(LOCAL_STACK_CONTAINER.getEndpointConfiguration(LocalStackContainer.Service.SQS))
                        .build());

        amazonSQS.createQueue("queue-name");

        return amazonSQS;
    }

    @Lazy
    @Bean
    AmazonSNSAsync amazonSNS() {
        startContainer();

        AmazonSNSAsync amazonSNS = AmazonSNSAsyncClientBuilder
                .standard()
                .withCredentials(AWS_CREDENTIALS_PROVIDER)
                .withEndpointConfiguration(LOCAL_STACK_CONTAINER.getEndpointConfiguration(LocalStackContainer.Service.SNS))
                .build();

        amazonSNS.createTopic("topic-name");

        return amazonSNS;
    }

    private static void startContainer() {
        if (!LOCAL_STACK_CONTAINER.isRunning()) {
            LOCAL_STACK_CONTAINER.start();
        }
    }
}
