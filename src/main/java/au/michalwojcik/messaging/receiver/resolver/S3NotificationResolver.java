package au.michalwojcik.messaging.receiver.resolver;

import au.michalwojcik.messaging.mapper.ReceiverMapper;
import au.michalwojcik.messaging.receiver.handler.S3NotificationHandler;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author michal-wojcik
 */
public final class S3NotificationResolver implements Resolver {

    private final List<S3NotificationHandler> s3NotificationHandlers;
    private final ReceiverMapper mapper;

    public S3NotificationResolver(List<S3NotificationHandler> s3NotificationHandlers, ReceiverMapper mapper) {
        this.s3NotificationHandlers = s3NotificationHandlers;
        this.mapper = mapper;
    }

    @Override
    public void resolve(Message<String> message) {
        for (JsonNode record : mapper.convertMessage(message).withArray("Records")) {
            Optional.of(record)
                    .map(node -> node.get("s3"))
                    .map(node -> node.get("object"))
                    .map(node -> node.get("key"))
                    .map(JsonNode::asText)
                    .ifPresent(resolveByKey(record));
        }
    }

    private Consumer<String> resolveByKey(JsonNode record) {
        return key -> s3NotificationHandlers.stream()
                .filter(s3NotificationHandler -> s3NotificationHandler.supports(key))
                .forEach(handleS3Notification(record));
    }

    private Consumer<S3NotificationHandler> handleS3Notification(JsonNode record) {
        return s3NotificationHandler -> {
            JavaType javaType = mapper.getMapper().constructType(S3EventNotification.S3EventNotificationRecord.class);
            s3NotificationHandler.handle(mapper.getMapper().convertValue(record, javaType));
        };
    }
}
