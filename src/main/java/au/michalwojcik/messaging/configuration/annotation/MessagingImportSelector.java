package au.michalwojcik.messaging.configuration.annotation;

import au.michalwojcik.messaging.configuration.AwsConfiguration;
import au.michalwojcik.messaging.receiver.Receiver;
import au.michalwojcik.messaging.receiver.resolver.NotificationResolver;
import au.michalwojcik.messaging.receiver.resolver.S3NotificationResolver;
import au.michalwojcik.messaging.sender.NotificationSender;
import io.awspring.cloud.autoconfigure.messaging.SnsAutoConfiguration;
import io.awspring.cloud.autoconfigure.messaging.SqsAutoConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author michal-wojcik
 */
public class MessagingImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableMessaging.class.getName());

        List<String> registeredClassNames = new ArrayList<>();

        registeredClassNames.add(AwsConfiguration.class.getName());

        List<ReceiveStrategy> receiveStrategies = Arrays.asList((ReceiveStrategy[]) attributes.get("receiver"));
        if (!receiveStrategies.isEmpty()) {
            registeredClassNames.add(SqsAutoConfiguration.class.getName());
            registeredClassNames.add(Receiver.class.getName());
            if (receiveStrategies.contains(ReceiveStrategy.NOTIFICATION)) {
                registeredClassNames.add(NotificationResolver.class.getName());
            }
            if (receiveStrategies.contains(ReceiveStrategy.S3)) {
                registeredClassNames.add(S3NotificationResolver.class.getName());
            }
        }

        List<SendStrategy> sendStrategies = Arrays.asList((SendStrategy[]) attributes.get("sender"));
        if (!sendStrategies.isEmpty()) {
            registeredClassNames.add(SnsAutoConfiguration.class.getName());
            if (sendStrategies.contains(SendStrategy.NOTIFICATION)) {
                registeredClassNames.add(NotificationSender.class.getName());
            }
        }

        return registeredClassNames.toArray(new String[0]);
    }
}
