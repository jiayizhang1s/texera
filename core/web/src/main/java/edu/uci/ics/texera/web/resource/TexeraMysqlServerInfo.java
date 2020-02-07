package edu.uci.ics.texera.web.resource;

public final class TexeraMysqlServerInfo {
    private final static String serverName = "root";
    private final static String password = "PassWithWord";
    private final static String url = "jdbc:mysql://localhost:3306/texera?serverTimezone=UTC";

    public static String getServername() {
        return serverName;
    }
    public static String getPassword() {
        return password;
    }
    public static String getUrl() {
        return url;
    }
}
