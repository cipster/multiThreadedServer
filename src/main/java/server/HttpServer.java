package server;

import java.nio.file.Path;

public interface HttpServer {
    void start();

    void stop();

    Path getFileDirectory();

    int getPort();

    boolean isServerRunning();

    ResponseDispatcher getDispatcher();
}
