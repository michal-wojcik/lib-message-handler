package au.michalwojcik.messaging.configuration.annotation;

import au.michalwojcik.messaging.configuration.AwsConfiguration;
import au.michalwojcik.messaging.mapper.ReceiverMapper;
import au.michalwojcik.messaging.mapper.SenderMapper;
import au.michalwojcik.messaging.receiver.Receiver;
import au.michalwojcik.messaging.receiver.resolver.NotificationResolver;
import au.michalwojcik.messaging.receiver.resolver.S3NotificationResolver;
import au.michalwojcik.messaging.sender.NotificationSender;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSAsync;
import io.awspring.cloud.autoconfigure.messaging.SnsAutoConfiguration;
import io.awspring.cloud.autoconfigure.messaging.SqsAutoConfiguration;
import io.awspring.cloud.core.region.RegionProvider;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author michal-wojcik
 */
class MessagingImportSelectorTest {

    @Test
    void shouldInjectEnableMessagingConfiguration() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withBean(SnsAutoConfiguration.class, () -> Mockito.mock(SnsAutoConfiguration.class))
                .withBean(AmazonSNSAsync.class, () -> Mockito.mock(AmazonSNSAsync.class))
                .withBean(SenderMapper.class, () -> Mockito.mock(SenderMapper.class))
                .withBean(SqsAutoConfiguration.class, () -> Mockito.mock(SqsAutoConfiguration.class))
                .withBean(SimpleMessageListenerContainer.class, () -> Mockito.mock(SimpleMessageListenerContainer.class))
                .withBean(ReceiverMapper.class, () -> Mockito.mock(ReceiverMapper.class))
                .withConfiguration(AutoConfigurations.of(EnableMessagingAnnotated.class));

        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AwsConfiguration.class);
            assertThat(context).hasSingleBean(RegionProvider.class);
            assertThat(context).hasSingleBean(AWSCredentialsProvider.class);
            assertThat(context).hasSingleBean(NotificationSender.class);

            assertThat(context).hasSingleBean(Receiver.class);
            assertThat(context).hasSingleBean(NotificationResolver.class);
            assertThat(context).hasSingleBean(S3NotificationResolver.class);
        });
    }

}