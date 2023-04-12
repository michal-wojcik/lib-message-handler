package au.michalwojcik.messaging.receiver.resolver;

import au.michalwojcik.messaging.Notification;
import au.michalwojcik.messaging.receiver.handler.NotificationHandler;
import au.michalwojcik.messaging.receiver.mapper.Mapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author michal-wojcik
 */
public final class NotificationResolver implements Resolver {

    private final List<NotificationHandler<?>> notificationHandlers;
    private final Mapper mapper;

    public NotificationResolver(List<NotificationHandler<?>> notificationHandlers, Mapper mapper) {
        this.notificationHandlers = notificationHandlers;
        this.mapper = mapper;
    }

    @Override
    public void resolve(Message<String> message) {
        JsonNode jsonNode = mapper.convertMessage(message);
        findNotificationName(jsonNode)
                .ifPresent(resolveByName(jsonNode));

    }

    private Consumer<String> resolveByName(JsonNode jsonNode) {
        return name -> notificationHandlers.stream()
                .filter(handler -> handler.supports(name))
                .forEach(handleNotification(jsonNode));
    }

    private Consumer<NotificationHandler<?>> handleNotification(JsonNode jsonNode) {
        return handler -> {
            JavaType javaType = mapper.getMapper().getTypeFactory().constructParametricType(
                    Notification.class,
                    handler.getType());

            JsonNode messageNode = jsonNode.get("Message");
            if (messageNode.isTextual()) {
                try {
                    handler.handle(mapper.getMapper().readValue(messageNode.textValue(), javaType));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                handler.handle(mapper.getMapper().convertValue(messageNode, javaType));
            }
        };
    }

    private Optional<String> findNotificationName(JsonNode jsonNode) {
        return Optional.of(jsonNode)
                .map(node -> node.get("Message"))
                .map(toNotification())
                .map(Notification::eventName);
    }

    private Function<JsonNode, Notification<?>> toNotification() {
        return node -> {
            try {
                return mapper.getMapper().readValue(node.asText(), Notification.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
