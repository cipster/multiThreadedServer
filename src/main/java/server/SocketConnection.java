package server;

import server.request.HttpRequest;
import server.request.HttpRequestParser;
import server.response.HttpResponse;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketConnection implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(SocketConnection.class.getName());
    private Socket socket;
    private HttpServer httpServer;
    private HttpRequestParser httpRequestParser;

    public SocketConnection(Socket socket, HttpServer httpServer, HttpRequestParser httpRequestParser) {
        this.socket = socket;
        this.httpServer = httpServer;
        this.httpRequestParser = httpRequestParser;
    }

    @Override
    public void run() {
        try (InputStream socketInputStream = socket.getInputStream();
             OutputStream socketOutputStream = socket.getOutputStream()) {

            HttpRequest httpRequest = httpRequestParser.parse(socketInputStream, httpServer.getFileDirectory());

            HttpResponse response = httpServer.dispatch(httpRequest);

            respond(response, socketOutputStream);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void respond(HttpResponse response, OutputStream out) throws IOException {
        BufferedOutputStream bufferedWriter = new BufferedOutputStream(out);
        bufferedWriter.write(response.toString().getBytes("UTF-8"));
        bufferedWriter.flush();
    }

}
