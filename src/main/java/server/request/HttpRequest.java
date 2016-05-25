package server.request;

import com.google.common.collect.Maps;
import org.apache.commons.fileupload.RequestContext;
import server.common.ContentType;
import server.common.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpRequest implements RequestContext {
    private HttpMethod method;
    private InputStream inputStream;
    private String url;
    private ContentType contentType;
    private int contentLength;
    private String protocol;
    private Map<String, String> header = Maps.newTreeMap();
    private byte[] body;

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

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public String getContentType() {
        return contentType.getEncoding();
    }

    protected void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
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

    public byte[] getBody() {
        return body;
    }

    protected void setBody(byte[] body) {
        this.body = body;
    }

    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }
}
