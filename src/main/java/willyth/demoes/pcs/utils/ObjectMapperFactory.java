package willyth.demoes.pcs.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.util.Collections;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class ObjectMapperFactory {

    public static ObjectMapper base() {
        return create(Collections.emptyMap(), Collections.emptyMap());
    }

    public static ObjectMapper scraper() {
        return create(Map.of(FAIL_ON_UNKNOWN_PROPERTIES, false), Collections.emptyMap());
    }

    private static ObjectMapper create(
            Map<DeserializationFeature, Boolean> deserializationFeatures,
            Map<SerializationFeature, Boolean> serializationFeatures) {
        final ObjectMapper objectMapper = new ObjectMapper();
        deserializationFeatures.forEach(objectMapper::configure);
        serializationFeatures.forEach(objectMapper::configure);
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }
}
