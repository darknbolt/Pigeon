package Models;

import Validations.Username;

public class Client {
    @Username
    private String username;

    private String ip;
    private int port;

    public Client(){}
    public Client(String username, int port, String ip) {
        this.username = username;
        this.port = port;
        this.ip = ip;
    }

    public String getUsername() { return username; }
    public String getIp() { return ip; }
    public int getPort() { return port; }

    public void setUsername(String username) { this.username = username; }
    public void setIp(String ip) { this.ip = ip; }
    public void setPort(int port) { this.port = port; }
}
