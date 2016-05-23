package server;


import server.common.HttpMethod;
import server.common.PageUtils;
import server.request.HttpRequest;
import server.request.HttpRequests;
import server.response.HttpResponse;
import server.response.HttpResponseBuilder;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketConnection implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(SocketConnection.class.getName());
    private Socket socket;
    private HttpServer httpServer;

    public SocketConnection(Socket socket, HttpServer httpServer) {
        this.socket = socket;
        this.httpServer = httpServer;
    }

    @Override
    public void run() {
        try (InputStream socketInputStream = socket.getInputStream();
             OutputStream socketOutputStream = socket.getOutputStream()) {

            HttpRequest httpRequest = HttpRequests.parse(socketInputStream);

            HttpResponse response;

            HttpMethod method = httpRequest.getMethod();
            if (method == HttpMethod.GET || method == HttpMethod.HEAD) {
                File serverFile = getFileForRequest(httpRequest);
                response = new HttpResponseBuilder(HttpResponse.StatusCode.OK).withRequestHeaders(httpRequest.getHeader()).withBody(serverFile).build();

            } else {
                response = new HttpResponseBuilder(HttpResponse.StatusCode.NOT_IMPLEMENTED).build();
            }

            respond(response, socketOutputStream);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private File getFileForRequest(HttpRequest httpRequest) {
        String requestedPath = httpRequest.getUrl().replaceAll("^/", "");

        if (requestedPath.isEmpty() || requestedPath.equalsIgnoreCase("index.html")) {
            requestedPath = PageUtils.indexPage();
        }
        return httpServer.getFileDirectory().resolve(requestedPath).toFile();
    }

    public void respond(HttpResponse response, OutputStream out) throws IOException {
        BufferedOutputStream bufferedWriter = new BufferedOutputStream(out);
        bufferedWriter.write(response.toString().getBytes("UTF-8"));
        bufferedWriter.flush();
    }

}
