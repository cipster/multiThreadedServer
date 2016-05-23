package server.common;

import java.util.EnumSet;

public enum ContentType {
    TEXT("text/plain"),
    HTML("text/html; charset=UTF-8"),
    JSON("application/json");

    private String encoding;

    ContentType(String encoding) {
        this.encoding = encoding;
    }

    public static ContentType parseFrom(String stringyEncoding) {
        for (ContentType contentType : EnumSet.allOf(ContentType.class)) {
            if (contentType.encoding.equals(stringyEncoding)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("ContentType not supported");
    }

    public String getEncoding() {
        return encoding;
    }
}
