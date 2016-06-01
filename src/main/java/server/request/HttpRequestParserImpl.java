package server.request;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import server.common.HttpMethod;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class HttpRequestParserImpl implements HttpRequestParser {

    @Override
    public HttpRequest parse(InputStream requestStream, Path filesDirectory) {
        try {
            BufferedReader requestReader = new BufferedReader(new InputStreamReader(requestStream));
            HttpRequest httpRequest;
            String line;
            List<String> requestHead;
            line = requestReader.readLine();
            requestHead = parseRequestHead(line);

            HttpMethod method = HttpMethod.valueOf(requestHead.get(0));

            httpRequest = new HttpRequest(method, requestHead.get(1), requestHead.get(2));

            line = requestReader.readLine();
            while (line != null && !line.isEmpty()) {
                Map<String, String> header = parseHeader(line);

                httpRequest.addHeader(header);
                line = requestReader.readLine();
            }

            if (method == HttpMethod.POST && isUpload(httpRequest)) {
                saveUploadedFile(filesDirectory, requestReader);
            }
            return httpRequest;
        } catch (IOException | IndexOutOfBoundsException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private boolean isUpload(HttpRequest httpRequest) {
        return httpRequest.getUrl().equals("/upload");
    }

    private void saveUploadedFile(Path filesDirectory, BufferedReader requestReader) throws IOException {
        String line;
        line = requestReader.readLine();
        if (isBoundary(line)) {
            line = requestReader.readLine();
            String metadata = getMetadata(line);
            String filename = getFilename(metadata);
            requestReader.readLine();
            requestReader.readLine();

            List<String> requestChars = Lists.newArrayList();
            while (requestReader.ready()) {
                requestChars.add(requestReader.readLine());
            }
            requestChars.remove(requestChars.size() - 1);
            String fileLine = Joiner.on(System.getProperty("line.separator")).join(requestChars);
            Path newFile = filesDirectory.resolve(filename);
            saveNewFile(fileLine.getBytes(), newFile);
        }
    }

    private String getFilename(String metadata) {
        return Splitter.on("=").splitToList(metadata).get(1).replaceAll("\"", "").replaceAll(" ", "_");
    }

    private String getMetadata(String line) {
        return Splitter.on(";").omitEmptyStrings().splitToList(line).get(2);
    }

    private boolean isBoundary(String line) {
        return line.contains("------");
    }

    private Map<String, String> parseHeader(String line) {
        List<String> header = Splitter.on(": ").omitEmptyStrings().limit(2).splitToList(line);

        if (header.size() != 2) {
            throw new IllegalStateException(String.format("Cannot parse header %s", line));
        }
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put(header.get(0), header.get(1));

        return headerMap;
    }

    private List<String> parseRequestHead(String line) {
        List<String> requestHead = null;
        if (line != null) {
            requestHead = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().limit(3).splitToList(line);
            if (requestHead.size() < 3) {
                throw new IllegalStateException("Request cannot be parsed");
            }
            if (!requestHead.get(2).startsWith("HTTP/")) {
                throw new IllegalStateException("Only HTTP requests are permitted");
            }
        }
        return requestHead;
    }

    private boolean saveNewFile(byte[] body, Path fileName) {
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
