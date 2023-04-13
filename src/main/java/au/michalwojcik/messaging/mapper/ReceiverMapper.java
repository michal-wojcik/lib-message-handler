package au.michalwojcik.messaging.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.Message;

/**
 * @author michal-wojcik
 */
public interface ReceiverMapper extends Mapper {

    JsonNode convertMessage(Message<String> message);
}
