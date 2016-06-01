package server;

import server.request.HttpRequest;
import server.response.HttpResponse;

import java.io.IOException;
import java.nio.file.Path;

public interface HttpServer {
    void start();

    void stop();

    Path getFileDirectory();

    int getPort();

    boolean isServerRunning();

    HttpResponse dispatch(HttpRequest httpRequest) throws IOException;
}
