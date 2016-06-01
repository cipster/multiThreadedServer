package server;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import server.common.HttpMethod;
import server.request.HttpRequest;
import server.request.HttpRequestParser;
import server.response.HttpResponse;
import server.response.HttpResponseBuilder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SocketConnectionTest {
    @Mock
    private Socket socket;
    @Mock
    private HttpServer httpServer;
    @Mock
    private HttpRequestParser httpRequestParser;
    @Mock
    private ResponseDispatcher dispatcher;

    private SocketConnection classUnderTest;

    private HttpRequest httpRequestStub;
    private HttpResponse httpResponseStub;

    @Before
    public void setUp() throws Exception {
        httpRequestStub = new HttpRequest(HttpMethod.GET, "", "HTTP/1.1");
        httpResponseStub = new HttpResponseBuilder(HttpResponse.StatusCode.OK).build();
        when(socket.getInputStream()).thenReturn(IOUtils.toInputStream("input"));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        when(httpServer.dispatch(any(HttpRequest.class))).thenReturn(httpResponseStub);

        classUnderTest = new SocketConnection(socket, httpServer, httpRequestParser);
    }


    @Test
    public void testRunOk() throws Exception {
        when(httpRequestParser.parse(any(InputStream.class), any(Path.class))).thenReturn(httpRequestStub);

        classUnderTest.run();
    }
}