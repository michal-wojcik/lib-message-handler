package au.michalwojcik.messaging.receiver.handler;

/**
 * @author michal-wojcik
 */
public sealed interface Handler<T> permits NotificationHandler, S3NotificationHandler {

    void handle(T t);

    boolean supports(String argument);
}
