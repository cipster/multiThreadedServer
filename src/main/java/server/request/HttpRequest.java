package server.request;

import com.google.common.collect.Maps;
import server.common.ContentType;
import server.common.HttpMethod;

import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String url;
    private ContentType contentType;
    private String protocol;
    private Map<String, String> header = Maps.newTreeMap();
    private String body;

    protected HttpRequest() {
    }

    public HttpMethod getMethod() {
        return method;
    }

    protected void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        this.url = url;
    }

    public ContentType getContentType() {
        return contentType;
    }

    protected void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getProtocol() {
        return protocol;
    }

    protected void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    protected void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    protected void setBody(String body) {
        this.body = body;
    }

    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }
}
