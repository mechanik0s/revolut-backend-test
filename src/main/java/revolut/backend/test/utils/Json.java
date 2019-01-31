package revolut.backend.test.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * utility classes are bad for OOP, but useful in some case
 */
public class Json {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
    }

    // prevent accidentally instantiation and inheritance
    private Json() {
    }

    public static String encodeAsString(Object obj) throws EncodeException {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
        }
    }

    public static byte[] encodeAsBytes(Object obj) throws EncodeException {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
        }
    }

    public static <T> T decodeValue(String src, Class<T> clazz) throws DecodeException {
        try {
            return mapper.readValue(src, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to decode: " + e.getMessage());
        }
    }

    public static <T> T decodeValue(byte[] src, Class<T> clazz) throws DecodeException {
        try {
            return mapper.readValue(src, clazz);
        } catch (IOException e) {
            // Yes, I want to wrap checked Jackson exceptions in unchecked for convenience
            throw new DecodeException("Failed to decode: " + e.getMessage());
        }
    }

    private static class EncodeException extends RuntimeException {
        public EncodeException(String message) {
            super(message);
        }

        public EncodeException(String message, Throwable cause) {
            super(message, cause);
        }

        public EncodeException() {
        }
    }

    private static class DecodeException extends RuntimeException {
        public DecodeException() {
        }

        public DecodeException(String message) {
            super(message);
        }

        public DecodeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
