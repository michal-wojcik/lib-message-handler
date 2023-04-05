package au.michalwojcik.messaging;

import java.time.LocalDateTime;

/**
 * @author michal-wojcik
 */
public record SimpleMessage(
        String id,
        LocalDateTime timestamp) {
}
