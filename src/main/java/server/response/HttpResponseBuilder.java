package server.response;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import server.common.ContentType;
import server.common.PageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class HttpResponseBuilder {
    public static final String FOUR_OH_FOUR = PageUtils.notFoundPage();
    private String status;
    private Map<String, String> headers = Maps.newTreeMap();
    private byte[] body = null;

    public HttpResponseBuilder(String status) {
        this.status = status;
    }

    public HttpResponseBuilder withHeader(String key, String value) {
        this.headers.put(key, value);

        return this;
    }

    public HttpResponseBuilder withDate() {
        String formattedDateTime = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        withHeader(HttpResponse.Headers.DATE, formattedDateTime);

        return this;
    }

    public HttpResponseBuilder withContentType(ContentType contentType) {
        withHeader(HttpResponse.Headers.CONTENT_TYPE, contentType.getEncoding());

        return this;
    }

    public HttpResponseBuilder withAttachmentContentDisposition(String filename) {
        withHeader(HttpResponse.Headers.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", filename));

        return this;
    }

    public HttpResponseBuilder withKeepAlive(boolean keepAlive) {
        String keepAliveValue;
        if (keepAlive) {
            keepAliveValue = "Keep-Alive";
        } else {
            keepAliveValue = "Close";
        }
        withHeader(HttpResponse.Headers.CONNECTION, keepAliveValue);

        return this;
    }

    public HttpResponseBuilder withContentLength(int length) {
        withHeader(HttpResponse.Headers.CONTENT_LENGTH, String.valueOf(length));

        return this;
    }

    public HttpResponseBuilder withBody(File file) {
        String filename = file.getName();
        if (file.exists() && file.isFile()) {
            try (FileInputStream reader = new FileInputStream(file)) {
                int length = (int) file.length();
                this.body = new byte[length];
                reader.read(body);

                withDate();
                withContentLength(length);
                if (filename.endsWith(".htm") || filename.endsWith(".html")) {
                    withContentType(ContentType.HTML);
                } else {
                    withContentType(ContentType.TEXT);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        } else {
            this.status = HttpResponse.StatusCode.NOT_FOUND;
            withBody(new File(FOUR_OH_FOUR));
        }

        return this;
    }

    private HttpResponseBuilder withBody(String htmlBody) {
        this.body = htmlBody.getBytes();

        return this;
    }

    public HttpResponseBuilder withRequestHeaders(Map<String, String> headers) {
        List<String> headersToPassOn = Lists.newArrayList(HttpResponse.Headers.HOST, HttpResponse.Headers.COOKIE);
        headers.entrySet()
                .stream()
                .filter(entry -> headersToPassOn.contains(entry.getKey()))
                .forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (key.equals(HttpResponse.Headers.HOST)) {
                        key = HttpResponse.Headers.SERVER;
                    }

                    withHeader(key, value);
                });

        return this;
    }

    public HttpResponse build() {
        withKeepAlive(true);
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(this.status);
        httpResponse.setHeaders(this.headers);
        httpResponse.setBody(this.body);

        return httpResponse;
    }
}