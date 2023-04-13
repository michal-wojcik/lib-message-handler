package au.michalwojcik.messaging.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public final class DeserializationMapper implements ReceiverMapper {

    private static final ObjectMapper DESERIALIZATION_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .build();

    @Override
    public ObjectMapper getMapper() {
        return DESERIALIZATION_MAPPER;
    }

    @Override
    public JsonNode convertMessage(Message<String> message) {
        try {
            return DESERIALIZATION_MAPPER.readTree(message.getPayload());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
