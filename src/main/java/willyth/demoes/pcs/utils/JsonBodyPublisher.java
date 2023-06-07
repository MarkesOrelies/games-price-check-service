package willyth.demoes.pcs.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;

public class JsonBodyPublisher {

    public static HttpRequest.BodyPublisher of(ObjectMapper objectMapper, Object object) throws JsonProcessingException {
        return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(object));
    }

}
