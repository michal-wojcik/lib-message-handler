package au.michalwojcik.messaging.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import io.awspring.cloud.core.region.RegionProvider;
import io.awspring.cloud.core.region.StaticRegionProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author michal-wojcik
 */
@Configuration(proxyBeanMethods = false)
public class AwsConfiguration {

    @Bean
    @ConditionalOnMissingBean(RegionProvider.class)
    RegionProvider regionProvider() {
        return new StaticRegionProvider(Regions.AP_SOUTHEAST_2.getName());
    }

    @Bean
    @ConditionalOnMissingBean(AWSCredentialsProvider.class)
    AWSCredentialsProvider awsCredentialsProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }
}
