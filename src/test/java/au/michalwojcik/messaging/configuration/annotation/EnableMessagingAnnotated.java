package au.michalwojcik.messaging.configuration.annotation;

import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * @author michal-wojcik
 */
@AutoConfiguration
@EnableMessaging(
        sender = SendStrategy.NOTIFICATION,
        receiver = {ReceiveStrategy.NOTIFICATION, ReceiveStrategy.S3}
)
public class EnableMessagingAnnotated {
}
