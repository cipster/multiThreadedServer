package server;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import server.common.Header;
import server.common.HttpMethod;
import server.request.HttpRequest;
import server.response.HttpResponse;

import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResponseDispatcherImplTest {

    @Mock
    private HttpServer httpServer;
    @Mock
    private HttpRequest httpRequest;

    @InjectMocks
    private ResponseDispatcherImpl classUnderTest;

    private Map<String, String> headerStub;

    @Before
    public void setUp() throws Exception {
        headerStub = Maps.newTreeMap();
        for (Header header : EnumSet.allOf(Header.class)) {
            headerStub.put(header.toString(), header.toString());
        }

        when(httpServer.getFileDirectory()).thenReturn(Paths.get("/test"));
        when(httpRequest.getUrl()).thenReturn("/test");
        when(httpRequest.getHeader()).thenReturn(headerStub);
    }

    @Test
    public void testDispatchWithNullRequest() throws Exception {
        HttpResponse response = classUnderTest.dispatch(null);

        assertThat("Response is 404 not found", response.getStatus(), is(HttpResponse.StatusCode.NOT_FOUND));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDispatchWithBadRequestNullMethod() throws Exception {
        when(httpRequest.getMethod()).thenReturn(null);

        classUnderTest.dispatch(httpRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDispatchWithBadRequestNullHeaders() throws Exception {
        when(httpRequest.getHeader()).thenReturn(null);
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        classUnderTest.dispatch(httpRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDispatchWithBadRequestNullUrl() throws Exception {
        when(httpRequest.getUrl()).thenReturn(null);
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        classUnderTest.dispatch(httpRequest);
    }

    @Test
    public void testDispatchWithGetUnknownPath() throws Exception {
        when(httpRequest.getUrl()).thenReturn("/unknown");
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        HttpResponse response = classUnderTest.dispatch(httpRequest);

        assertThat("Response is 404 Not Found", response.getStatus(), is(HttpResponse.StatusCode.NOT_FOUND));
    }

    @Test
    public void testDispatchWithGetIndex() throws Exception {
        when(httpRequest.getUrl()).thenReturn("/index.html");
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        HttpResponse response = classUnderTest.dispatch(httpRequest);
        assertThat("Response is 200 OK", response.getStatus(), is(HttpResponse.StatusCode.OK));
    }

    @Test
    public void testDispatchWithGetFileUpload() throws Exception {
        when(httpRequest.getUrl()).thenReturn("/file-upload.html");
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        HttpResponse response = classUnderTest.dispatch(httpRequest);
        assertThat("Response is 200 OK", response.getStatus(), is(HttpResponse.StatusCode.OK));
    }

    @Test
    public void testDispatchWithGetUploadedFile() throws Exception {
        when(httpRequest.getUrl()).thenReturn("/test.html");
        when(httpServer.getFileDirectory()).thenReturn(Paths.get("./src/test/resources"));
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        HttpResponse response = classUnderTest.dispatch(httpRequest);
        assertThat("Response is 200 OK", response.getStatus(), is(HttpResponse.StatusCode.OK));
    }

    @Test
    public void testDispatchWithPostUnknownPath() throws Exception {
        when(httpRequest.getUrl()).thenReturn("/unknown");
        when(httpRequest.getMethod()).thenReturn(HttpMethod.POST);

        HttpResponse response = classUnderTest.dispatch(httpRequest);

        assertThat("Response is 404 Not Found", response.getStatus(), is(HttpResponse.StatusCode.NOT_FOUND));
    }

    @Test
    public void testDispatchWithPostFile() throws Exception {
        when(httpRequest.getUrl()).thenReturn("/upload");
        when(httpRequest.getMethod()).thenReturn(HttpMethod.POST);

        HttpResponse response = classUnderTest.dispatch(httpRequest);
        assertThat("Response is 200 OK", response.getStatus(), is(HttpResponse.StatusCode.OK));
    }

    @Test
    public void testDispatchWithPut() throws Exception {
        when(httpRequest.getMethod()).thenReturn(HttpMethod.PUT);

        HttpResponse response = classUnderTest.dispatch(httpRequest);

        assertThat("Response is 404 Not Found", response.getStatus(), is(HttpResponse.StatusCode.NOT_FOUND));
    }

}