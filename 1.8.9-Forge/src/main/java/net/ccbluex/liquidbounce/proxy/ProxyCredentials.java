package net.ccbluex.liquidbounce.proxy;

public class ProxyCredentials {

    private String username, password;

    public ProxyCredentials(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
