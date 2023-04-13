package au.michalwojcik.messaging.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author michal-wojcik
 */
public final class SerializationMapper implements SenderMapper {

    private static final ObjectMapper SERIALIZATION_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .build();

    @Override
    public ObjectMapper getMapper() {
        return SERIALIZATION_MAPPER;
    }
}
