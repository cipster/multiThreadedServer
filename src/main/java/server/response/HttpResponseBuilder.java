package server.response;

import com.google.common.collect.Maps;
import server.common.ContentType;
import server.common.Header;
import server.common.PageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
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

    public HttpResponseBuilder withStatus(String status) {
        this.status = status;

        return this;
    }

    public HttpResponseBuilder withDate() {
        String formattedDateTime = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        withHeader(Header.DATE.toString(), formattedDateTime);

        return this;
    }

    public HttpResponseBuilder withContentType(ContentType contentType) {
        withHeader(Header.CONTENT_TYPE.toString(), contentType.getEncoding());

        return this;
    }

    public HttpResponseBuilder withAttachmentContentDisposition(String filename) {
        withHeader(Header.CONTENT_DISPOSITION.toString(), String.format("attachment; filename=\"%s\"", filename));

        return this;
    }

    public HttpResponseBuilder withKeepAlive(boolean keepAlive) {
        String keepAliveValue;
        if (keepAlive) {
            keepAliveValue = "Keep-Alive";
        } else {
            keepAliveValue = "Close";
        }
        withHeader(Header.CONNECTION.toString(), keepAliveValue);

        return this;
    }

    public HttpResponseBuilder withContentLength(int length) {
        withHeader(Header.CONTENT_LENGTH.toString(), String.valueOf(length));

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
                if (filename.startsWith("tempIndex")) {
                    boolean deletedTempIndexFile = file.delete();
                    if (!deletedTempIndexFile) {
                        file.deleteOnExit();
                    }
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

    public HttpResponseBuilder withRequestHeaders(Map<String, String> headers) {
        if (headers == null) {
            throw new IllegalArgumentException("HTTP Request headers must not be null");
        }
        EnumSet<Header> headersToPassOn = EnumSet.of(Header.HOST, Header.COOKIE);
        headers.entrySet()
                .stream()
                .filter(entry -> headersToPassOn.contains(Header.parseFrom(entry.getKey())))
                .forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (key.equals(Header.HOST.toString())) {
                        key = Header.SERVER.toString();
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