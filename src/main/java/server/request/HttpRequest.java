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
    private Map<String, String> header = Maps.newLinkedHashMap();

    public HttpRequest(HttpMethod method, String url, String protocol) {
        this.method = method;
        this.url = url;
        this.protocol = protocol;
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

    public String getContentType() {
        return contentType.getEncoding();
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

    public void addHeader(Map<String, String> header) {
        this.header.putAll(header);
    }
}
