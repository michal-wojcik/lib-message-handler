package au.michalwojcik.messaging;

import java.time.LocalDateTime;

/**
 * @author michal-wojcik
 */
public record SimpleNotification(
        String id,
        LocalDateTime timestamp) {
}
