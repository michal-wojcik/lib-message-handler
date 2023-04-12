package au.michalwojcik.messaging.receiver;

import au.michalwojcik.messaging.Notification;
import au.michalwojcik.messaging.SimpleNotification;
import au.michalwojcik.messaging.receiver.handler.NotificationHandler;

/**
 * @author michal-wojcik
 */
final class SimpleNotificationHandler implements NotificationHandler<Notification<SimpleNotification>> {

    @Override
    public void handle(Notification<SimpleNotification> simpleNotificationNotification) {

    }

    @Override
    public boolean supports(String argument) {
        return "simple-notification".equals(argument);
    }

    @Override
    public Class<?> getType() {
        return SimpleNotification.class;
    }
}
