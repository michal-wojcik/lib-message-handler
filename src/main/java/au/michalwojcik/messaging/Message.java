package au.michalwojcik.messaging;

/**
 * @author michal-wojcik
 */
public record Message<T>(T event, String eventName) {
}
