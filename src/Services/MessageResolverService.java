package Services;

import Validations.Username;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

public class MessageResolverService {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    public static String resolveMessage(Class<?> clazz, String fieldName, String errorType) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        Username annotation = field.getAnnotation(Username.class);

        if (annotation == null) return null;

        String key = switch (errorType){
            case "tooShort" -> annotation.tooShortError();
            case "tooLong" -> annotation.tooLongError();
            default -> throw new IllegalArgumentException("Invalid error type: " + errorType);
        };

        return messages.containsKey(key) ? messages.getString(key) : "Missing message for key: " + key;
    }
}
