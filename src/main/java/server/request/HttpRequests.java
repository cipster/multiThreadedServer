package server.request;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import server.common.HttpMethod;

import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class HttpRequests {

    public static HttpRequest parse(InputStream requestStream, Path filesDirectory) {
        try {
            BufferedReader requestReader = new BufferedReader(new InputStreamReader(requestStream));
            HttpRequest httpRequest;
            String line;
            List<String> requestHead;
            line = requestReader.readLine();
            requestHead = parseRequestHead(line);

            HttpMethod method = HttpMethod.valueOf(requestHead.get(0));

            httpRequest = new HttpRequest();
            httpRequest.setMethod(method);
            httpRequest.setUrl(requestHead.get(1));
            httpRequest.setProtocol(requestHead.get(2));

            line = requestReader.readLine();
            while (line != null && !line.isEmpty()) {
                List<String> header = parseHeader(line);

                httpRequest.addHeader(header.get(0), header.get(1));
                line = requestReader.readLine();
            }

            if (method == HttpMethod.POST && httpRequest.getUrl().equals("/upload")) {
                line = requestReader.readLine();
                if (line.contains("------")) {
                    line = requestReader.readLine();
                    String metadata = Splitter.on(";").omitEmptyStrings().splitToList(line).get(2);
                    String fileName = Splitter.on("=").splitToList(metadata).get(1).replaceAll("\"", "").replaceAll(" ", "_");
                    requestReader.readLine();
                    requestReader.readLine();

                    List<String> requestChars = Lists.newArrayList();
                    while (requestReader.ready()) {
                        requestChars.add(requestReader.readLine());
                    }
                    requestChars.remove(requestChars.size() - 1);
                    String fileLine = Joiner.on("\n").join(requestChars);
                    Path newFile = filesDirectory.resolve(fileName);
                    saveNewFile(fileLine.getBytes(), newFile);
                }
            }
            return httpRequest;
        } catch (IOException | IndexOutOfBoundsException e) {
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

    private static boolean saveNewFile(byte[] body, Path fileName) {
        boolean fileWasCreated;
        try {
            File file = fileName.toFile();
            fileWasCreated = file.createNewFile();
            FileUtils.writeByteArrayToFile(file, body);
        } catch (IOException e) {
            fileWasCreated = false;
        }

        return fileWasCreated;
    }
}
