package Services;

import Models.Client;
import Validations.Username;
import java.lang.reflect.Field;

public class UserService {
    public String createClient(String username, String ip, int port){
        for (Field field : Client.class.getDeclaredFields()){
            if (field.isAnnotationPresent(Username.class)){
                int maxLen = field.getAnnotation(Username.class).max();
                int minLen = field.getAnnotation(Username.class).min();
                int usernameLen = username.length();

                try {
                    if (usernameLen > maxLen) return MessageResolverService.resolveMessage(Client.class, "username", "tooLong");
                    if (usernameLen < minLen) return MessageResolverService.resolveMessage(Client.class, "username", "tooShort");
                    return "Success";
                }catch (NoSuchFieldException e){
                    e.printStackTrace();
                }
            }
        }

        return "Something went wrong";
    }
}
