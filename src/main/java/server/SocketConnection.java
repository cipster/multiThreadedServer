package server;

import server.request.HttpRequest;
import server.request.HttpRequests;
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
    private ResponseDispatcher dispatcher;

    public SocketConnection(Socket socket, HttpServer httpServer) {
        this.socket = socket;
        this.httpServer = httpServer;
        this.dispatcher = this.httpServer.getDispatcher();
    }

    @Override
    public void run() {
        try (InputStream socketInputStream = socket.getInputStream();
             OutputStream socketOutputStream = socket.getOutputStream()) {

            HttpRequest httpRequest = HttpRequests.parse(socketInputStream, httpServer.getFileDirectory());

            HttpResponse response = dispatcher.dispatch(httpRequest);

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
