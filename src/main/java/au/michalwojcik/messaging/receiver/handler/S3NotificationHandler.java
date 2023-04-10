package au.michalwojcik.messaging.receiver.handler;

import com.amazonaws.services.s3.event.S3EventNotification;

/**
 * @author michal-wojcik
 */
public non-sealed interface S3NotificationHandler extends Handler<S3EventNotification.S3EventNotificationRecord> {
}
