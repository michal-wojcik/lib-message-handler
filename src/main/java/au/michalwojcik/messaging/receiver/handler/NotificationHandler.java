package au.michalwojcik.messaging.receiver.handler;

/**
 * @author michal-wojcik
 */
public non-sealed interface NotificationHandler<T> extends Handler<T> {

    Class<?> getType();
}
