package server;

import org.apache.commons.io.FileUtils;
import server.common.HttpMethod;
import server.common.PageUtils;
import server.request.HttpRequest;
import server.response.HttpResponse;
import server.response.HttpResponseBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResponseDispatcherImpl implements ResponseDispatcher {
    public static final String STARTING_WITH_FORWARD_SLASH = "^/";
    public static final String TEMP_INDEX_NAME = "tempIndex";
    private HttpServer httpServer;

    public ResponseDispatcherImpl(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    @Override
    public HttpResponse dispatch(HttpRequest httpRequest) throws IOException {
        HttpResponse response;
        HttpMethod method = httpRequest.getMethod();
        switch (method) {
            case HEAD:
            case GET:
                response = buildGetResponse(httpRequest);
                break;
            case POST:
                response = buildPostResponse(httpRequest);
                break;
            default:
                response = new HttpResponseBuilder(HttpResponse.StatusCode.NOT_FOUND)
                        .withBody(new File(PageUtils.notFoundPage()))
                        .build();

        }

        return response;
    }

    private HttpResponse buildPostResponse(HttpRequest httpRequest) {
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponse.StatusCode.OK)
                .withRequestHeaders(httpRequest.getHeader());
        File pageFile;
        String requestedPath = httpRequest.getUrl().replaceAll(STARTING_WITH_FORWARD_SLASH, "");
        switch (requestedPath) {
            case "upload":
                pageFile = new File(PageUtils.fileUploadSuccessPage());
                break;
            default:
                pageFile = new File(PageUtils.notFoundPage());
        }
        responseBuilder.withBody(pageFile);
        return responseBuilder.build();
    }

    private HttpResponse buildGetResponse(HttpRequest httpRequest) throws IOException {
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponse.StatusCode.OK)
                .withRequestHeaders(httpRequest.getHeader());
        File pageFile;
        String requestedPath = httpRequest.getUrl().replaceAll(STARTING_WITH_FORWARD_SLASH, "");
        switch (requestedPath) {
            case "":
            case "index.html":
                pageFile = buildIndexPage();
                break;
            case "file-upload.html":
                pageFile = new File(PageUtils.fileUploadPage());
                break;
            case "upload":
                pageFile = new File(PageUtils.fileUploadSuccessPage());
                break;
            default:
                pageFile = httpServer.getFileDirectory().resolve(requestedPath).toFile();
                responseBuilder.withAttachmentContentDisposition(pageFile.getName());
        }
        responseBuilder.withBody(pageFile);
        return responseBuilder.build();
    }

    private File buildIndexPage() throws IOException {
        File pageFile = new File(PageUtils.indexPage());
        List<File> uploadedFiles = PageUtils.getUploadedFiles(httpServer.getFileDirectory());
        StringBuilder stringBuilder = new StringBuilder();
        if (uploadedFiles.isEmpty()) {
            stringBuilder.append("<h3 class='text-warning'>No files here. Upload some by pressing the button below</h3>");
        }
        uploadedFiles
                .stream()
                .map(File::getName)
                .forEach(filename -> stringBuilder
                        .append("<li class='list-group-item'><a target='_blank' href='/").append(filename).append("'>")
                        .append(filename)
                        .append("</a></li>"));

        String replacedIndexPage = FileUtils.readFileToString(pageFile).replace("{{replaceable}}", stringBuilder.toString());
        pageFile = File.createTempFile(TEMP_INDEX_NAME, ".html");
        FileUtils.writeStringToFile(pageFile, replacedIndexPage);

        return pageFile;
    }

}
