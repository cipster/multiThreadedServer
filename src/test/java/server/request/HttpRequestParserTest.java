package server.request;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import server.common.HttpMethod;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class HttpRequestParserTest {

    private String filename = "test.xml";
    private InputStream getInputStreamStub;
    private InputStream postInputStreamStub;
    private InputStream badInputStreamStub;
    private InputStream badInputStreamStub2;
    private Path pathStub;

    private HttpRequestParserImpl classUnderTest;

    @Before
    public void setUp() throws Exception {
        classUnderTest = new HttpRequestParserImpl();

        String stringyGetRequest = FileUtils.readFileToString(new File("./src/test/resources/getRequest.txt"));
        getInputStreamStub = IOUtils.toInputStream(stringyGetRequest);

        String stringyPostRequest = FileUtils.readFileToString(new File("./src/test/resources/postRequest.txt"));
        postInputStreamStub = IOUtils.toInputStream(stringyPostRequest);

        String stringyBadRequest = FileUtils.readFileToString(new File("./src/test/resources/badRequest.txt"));
        badInputStreamStub = IOUtils.toInputStream(stringyBadRequest);

        String stringyBadRequest2 = FileUtils.readFileToString(new File("./src/test/resources/badRequest2.txt"));
        badInputStreamStub2 = IOUtils.toInputStream(stringyBadRequest2);

        String tempDir = System.getProperty("java.io.tmpdir");
        pathStub = Paths.get(tempDir);
    }

    @Test
    public void testParseGetIsOK() {
        HttpRequest httpRequest = classUnderTest.parse(getInputStreamStub, pathStub);

        assertThat("Method should be GET", httpRequest.getMethod(), is(HttpMethod.GET));
        assertThat("Protocol should be HTTP/1.1", httpRequest.getProtocol(), is("HTTP/1.1"));
    }

    @Test
    public void testParsePostIsOK() {
        HttpRequest httpRequest = classUnderTest.parse(postInputStreamStub, pathStub);

        File uploadedFile = pathStub.resolve(filename).toFile();

        assertThat("Method should be POST", httpRequest.getMethod(), is(HttpMethod.POST));
        assertThat("Protocol should be HTTP/1.1", httpRequest.getProtocol(), is("HTTP/1.1"));
        assertThat("filename is test.xml and was added", uploadedFile.exists());

        uploadedFile.deleteOnExit();
    }

    @Test(expected = IllegalStateException.class)
    public void testParseNotHttpRequest() throws Exception {
        classUnderTest.parse(badInputStreamStub, pathStub);
    }

    @Test(expected = IllegalStateException.class)
    public void testParseInsufficientRequestInfo() throws Exception {
        classUnderTest.parse(badInputStreamStub2, pathStub);
    }
}