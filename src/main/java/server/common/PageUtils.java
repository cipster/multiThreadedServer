package server.common;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class PageUtils {
    private static final Logger LOGGER = Logger.getLogger(PageUtils.class.getName());

    public static String indexPage() {
        return getPage("index.html");
    }

    public static String notFoundPage() {
        return getPage("not-found.html");
    }

    public static String fileUploadPage() {
        return getPage("file-upload.html");
    }

    public static String fileUploadSuccessPage() {
        return getPage("file-upload-success.html");
    }

    private static String getPage(String pageName) {
        String page = "";
        URL pageLocation = PageUtils.class.getClassLoader().getResource(pageName);
        if (pageLocation != null) {
            page = pageLocation.getPath();
        }

        return page;
    }

    public static List<File> getUploadedFiles(Path fileDirectory) {
        List<File> files = Lists.newArrayList();
        try {
            Files.list(fileDirectory)
                    .forEach(path -> files.add(path.toFile()));
        } catch (IOException e) {
            LOGGER.severe("Error reading files from file directory");
        }
        return files;
    }

}
