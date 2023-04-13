package au.michalwojcik.messaging.sender;

import au.michalwojcik.messaging.Notification;
import au.michalwojcik.messaging.mapper.SenderMapper;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.fasterxml.jackson.databind.JsonNode;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author michal-wojcik
 */
public class NotificationSender {

    private final NotificationMessagingTemplate notificationMessagingTemplate;
    private final SenderMapper mapper;
    private final String topicName;

    public NotificationSender(
            AmazonSNSAsync amazonSNSAsync,
            SenderMapper mapper,
            @Value("${notification.sender.topic.name}") String topicName) {
        this.notificationMessagingTemplate = new NotificationMessagingTemplate(amazonSNSAsync);
        this.mapper = mapper;
        this.topicName = topicName;
    }

    public void send(Object event, String eventName) {
        send(event, eventName, topicName);
    }

    public void send(Object event, String eventName, String topicName) {
        Notification<Object> notification = new Notification<>(event, eventName);
        notificationMessagingTemplate.convertAndSend(
                topicName,
                (JsonNode) mapper.getMapper().valueToTree(notification));
    }
}
