package au.michalwojcik.messaging.configuration.annotation;

import au.michalwojcik.messaging.configuration.AwsConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import io.awspring.cloud.core.region.RegionProvider;
import org.junit.jupiter.api.Test;
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
                .withConfiguration(AutoConfigurations.of(EnableMessagingAnnotated.class));

        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AwsConfiguration.class);
            assertThat(context).hasSingleBean(RegionProvider.class);
            assertThat(context).hasSingleBean(AWSCredentialsProvider.class);
        });
    }

}