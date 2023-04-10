package au.michalwojcik.messaging.receiver.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.Message;

/**
 * @author michal-wojcik
 */
public class MessageMapper implements Mapper {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .build();

    @Override
    public ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }

    @Override
    public JsonNode convertMessage(Message<String> message) {
        try {
            return OBJECT_MAPPER.readTree(message.getPayload());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
