package Services;

import Models.Client;
import Validations.Username;

public class UserService {
    public String createClient(String username, String ip, int port){
        Client client = new Client(username, port, ip);
        //TODO: Retention Check in Client Constructor
        /*if () return null;
        else return "Some Error";*/
        return "";
    }
}
