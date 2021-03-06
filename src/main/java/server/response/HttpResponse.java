package server.response;

import com.google.common.collect.Maps;

import java.util.Map;

public class HttpResponse {
    private static final String protocol = "HTTP/1.1";
    private String status;
    private Map<String, String> headers = Maps.newLinkedHashMap();
    private byte[] body = null;

    public static String getProtocol() {
        return protocol;
    }

    public String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(String.format("%s %s \r\n", protocol, status));
        for (String key : headers.keySet()) {
            result.append(String.format("%s: %s \r\n", key, headers.get(key)));
        }
        result.append("\r\n");
        if (body != null) {
            result.append(new String(body));
        }

        return result.toString();
    }

    public static class StatusCode {
        public static final String OK = "200 OK";
        public static final String NOT_FOUND = "404 Not Found";
    }
}
