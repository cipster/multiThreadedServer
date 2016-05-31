package server.request;

import java.io.InputStream;
import java.nio.file.Path;

public interface HttpRequestParser {
    HttpRequest parse(InputStream requestStream, Path filesDirectory);
}
