package willyth.demoes.pcs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {

    private final Class<T> target;
    private final ObjectMapper objectMapper;

    public JsonBodyHandler(Class<T> target, ObjectMapper objectMapper) {
        this.target = target;
        this.objectMapper = objectMapper;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        var upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);

        return HttpResponse.BodySubscribers.mapping(
                upstream,
                (body) -> {
                    try {
                        return objectMapper.readValue(body, target);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
        );
    }
}
