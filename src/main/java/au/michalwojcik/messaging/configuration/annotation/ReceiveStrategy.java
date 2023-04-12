package au.michalwojcik.messaging.configuration.annotation;

/**
 * Some receiving strategies.
 *
 * @author michal-wojcik
 */
public enum ReceiveStrategy {

    /**
     * Enables default notification handler
     */
    NOTIFICATION,
    /**
     * Enables S3 notification event
     */
    S3
}
