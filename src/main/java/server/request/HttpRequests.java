package server.request;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import server.common.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class HttpRequests {


    public static HttpRequest parse(InputStream requestStream) {
        try {
            BufferedReader requestReader = new BufferedReader(new InputStreamReader(requestStream));
            HttpRequest httpRequest;
            String line;
            List<String> requestHead;
            StringBuilder bodyBuilder = new StringBuilder();
            line = requestReader.readLine();
            requestHead = parseRequestHead(line);

            httpRequest = new HttpRequest();
            httpRequest.setMethod(HttpMethod.valueOf(requestHead.get(0)));
            httpRequest.setUrl(requestHead.get(1));
            httpRequest.setProtocol(requestHead.get(2));

            line = requestReader.readLine();
            while (line != null && !line.isEmpty()) {
                List<String> header = parseHeader(line);

                httpRequest.addHeader(header.get(0), header.get(1));
                line = requestReader.readLine();
            }
            while (requestReader.ready()) {
                line = requestReader.readLine();
                bodyBuilder.append(line);
            }

            httpRequest.setBody(bodyBuilder.toString());
            return httpRequest;
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static List<String> parseHeader(String line) {
        List<String> header = Splitter.on(": ").omitEmptyStrings().limit(2).splitToList(line);
        if (header.size() != 2) {
            throw new IllegalStateException(String.format("Cannot parse header %s", line));
        }

        return header;
    }

    private static List<String> parseRequestHead(String line) {
        List<String> requestHead;
        if (line != null) {
            requestHead = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().limit(3).splitToList(line);
            if (requestHead.size() < 3) {
                throw new IllegalStateException("Request cannot be parsed");
            }
            if (!requestHead.get(2).startsWith("HTTP/")) {
                throw new IllegalStateException("Only HTTP requests are permitted");
            }
        } else {
            requestHead = Collections.emptyList();
        }

        return requestHead;
    }
}
