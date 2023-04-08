package au.michalwojcik.messaging.receiver.resolver;

/**
 * @author michal-wojcik
 */
public sealed interface Resolver permits MessageResolver, S3NotificationResolver {

}
