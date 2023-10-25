package ru.adel.server;

/**
 * Application starting point
 */
public class ServerApplication {
    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private static final int MAX_AUTHENTICATION_ATTEMPTS = 3;

    public static void main(String[] args) {
        Server server = new Server(HOST, PORT, MAX_AUTHENTICATION_ATTEMPTS);
        server.start();
    }
}
