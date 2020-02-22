package net.ccbluex.liquidbounce.proxy;

public class Proxy {

    private String host;
    private int port;

    private ProxyCredentials proxyCredentials;

    public Proxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Proxy(String host, int port, ProxyCredentials proxyCredentials) {
        this.host = host;
        this.port = port;
        this.proxyCredentials = proxyCredentials;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setProxyCredentials(ProxyCredentials proxyCredentials) {
        this.proxyCredentials = proxyCredentials;
    }

    public ProxyCredentials getProxyCredentials() {
        return proxyCredentials;
    }

    public boolean hasProxyCredentials() {
        return proxyCredentials != null;
    }

    public boolean isSame(Proxy proxy) {
        return getHost().equalsIgnoreCase(proxy.getHost()) && getPort() == proxy.getPort() && (hasProxyCredentials() ? getProxyCredentials().getUsername().equals(proxy.getProxyCredentials().getUsername()) : hasProxyCredentials() == proxy.hasProxyCredentials());
    }
}
