package au.michalwojcik.messaging;

/**
 * @author michal-wojcik
 */
public record Notification<T>(T event, String eventName) {
}
