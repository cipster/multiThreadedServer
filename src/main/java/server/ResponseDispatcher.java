package server;

import server.request.HttpRequest;
import server.response.HttpResponse;

import java.io.IOException;

public interface ResponseDispatcher {
    HttpResponse dispatch(HttpRequest httpRequest) throws IOException;
}
