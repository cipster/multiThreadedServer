package server.common;

import java.util.EnumSet;

public enum Header {
    DATE("Date"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_DISPOSITION("Content-Disposition"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Keep-Alive"),
    SERVER("Server"),
    HOST("Host"),
    COOKIE("Cookie");

    private String headerName;

    Header(String headerName) {
        this.headerName = headerName;
    }

    public static Header parseFrom(String stringyHeader) {
        for (Header header : EnumSet.allOf(Header.class)) {
            if (header.toString().equals(stringyHeader)) {
                return header;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.headerName;
    }
}
