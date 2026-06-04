package com.batu.zfile.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class CursorUtils {

    private final ObjectMapper objectMapper;

    public CursorUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public KeysetScrollPosition getPosition(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return ScrollPosition.keyset();
        }

        return decodeCursorPosition(cursor);
    }

    public String getNextCursor(Window<?> window) {
        if (window == null || window.isEmpty() || window.isLast()) {
            return null;
        }

        KeysetScrollPosition nextPosition =
                (KeysetScrollPosition) window.positionAt(window.size() - 1);

        return encodeCursorPosition(nextPosition);
    }

    private String encodeCursorPosition(KeysetScrollPosition position) {
        try {
            String json = objectMapper.writeValueAsString(position.getKeys());

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));

        } catch (Exception ex) {
            throw new IllegalStateException("Could not encode cursor", ex);
        }
    }

    private KeysetScrollPosition decodeCursorPosition(String cursor) {
        try {
            String json = new String(
                    Base64.getUrlDecoder().decode(cursor),
                    StandardCharsets.UTF_8
            );

            Map<String, Object> keys = objectMapper.readValue(
                    json,
                    new TypeReference<>() {}
            );

            return ScrollPosition.forward(keys);

        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid cursor", ex);
        }
    }
}