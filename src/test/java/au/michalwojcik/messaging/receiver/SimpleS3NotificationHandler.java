package au.michalwojcik.messaging.receiver;

import au.michalwojcik.messaging.receiver.handler.S3NotificationHandler;
import com.amazonaws.services.s3.event.S3EventNotification;

/**
 * @author michal-wojcik
 */
final class SimpleS3NotificationHandler implements S3NotificationHandler {

    @Override
    public void handle(S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord) {

    }

    @Override
    public boolean supports(String argument) {
        return argument.startsWith("path/to/");
    }
}
