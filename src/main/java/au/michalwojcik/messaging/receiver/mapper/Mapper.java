package au.michalwojcik.messaging.receiver.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;

/**
 * @author michal-wojcik
 */
public interface Mapper {

    ObjectMapper getMapper();

    JsonNode convertMessage(Message<String> message);
}
