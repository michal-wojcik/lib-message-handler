package au.michalwojcik.messaging.sender;

import au.michalwojcik.messaging.Message;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author michal-wojcik
 */
public class Sender {

    private static final ObjectMapper OBJECT_MAPPER_SENDER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .build();

    private final NotificationMessagingTemplate notificationMessagingTemplate;
    private final String topicName;

    public Sender(
            AmazonSNSAsync amazonSNSAsync,
            @Value("${message.sender.topic.name}") String topicName) {
        this.notificationMessagingTemplate = new NotificationMessagingTemplate(amazonSNSAsync);
        this.topicName = topicName;
    }

    public void send(Object event, String eventName) {
        send(event, eventName, topicName);
    }

    public void send(Object event, String eventName, String topicName) {
        Message<Object> message = new Message<>(event, eventName);
        notificationMessagingTemplate.convertAndSend(
                topicName,
                (JsonNode) OBJECT_MAPPER_SENDER.valueToTree(message));
    }
}
