package au.michalwojcik.messaging.receiver.handler;

/**
 * @author michal-wojcik
 */
public sealed interface Handler permits MessageHandler, S3NotificationHandler {

    void handle();
}
