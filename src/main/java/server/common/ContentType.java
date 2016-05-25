package server.common;

public enum ContentType {
    TEXT("text/plain"),
    HTML("text/html; charset=UTF-8");

    private String encoding;

    ContentType(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }
}
