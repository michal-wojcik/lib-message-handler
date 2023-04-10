package au.michalwojcik.messaging.receiver.resolver;

import org.springframework.messaging.Message;

/**
 * @author michal-wojcik
 */
public sealed interface Resolver permits NotificationResolver, S3NotificationResolver {

    void resolve(Message<String> message);
}
