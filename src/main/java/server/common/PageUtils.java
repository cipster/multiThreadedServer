package server.common;

import java.net.URL;

public class PageUtils {

    public static String indexPage() {
        return getPage("index.html");
    }

    public static String notFoundPage() {
        return getPage("not-found.html");
    }

    private static String getPage(String pageName) {
        String page = "";
        URL pageLocation = PageUtils.class.getClassLoader().getResource(pageName);
        if (pageLocation != null) {
            page = pageLocation.getPath();
        }

        return page;
    }
}
