package server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import server.common.HttpMethod;
import server.request.HttpRequest;
import server.request.HttpRequestParser;

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
    private ResponseDispatcher dispatcher;
    @Mock
    private HttpRequestParser httpRequestParser;

    @InjectMocks
    private SocketConnection classUnderTest;

    private HttpRequest httpRequestStub;
    private Path pathStub;

    @Before
    public void setUp() throws Exception {
        httpRequestStub = new HttpRequest(HttpMethod.GET, "", "HTTP/1.1");

    }

    @Test
    public void testRunOk() throws Exception {
//        String tempDir = System.getProperty("java.io.tmpdir");
//        pathStub = Paths.get(tempDir);
//        when(socket.getInputStream()).thenReturn(IOUtils.toInputStream("input"));
//        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
//        when(httpServer.getFileDirectory()).thenReturn(pathStub);
        dispatcher = new ResponseDispatcherImpl(httpServer);

        when(httpServer.getDispatcher()).thenReturn(dispatcher);
        when(httpRequestParser.parse(any(), any())).thenReturn(httpRequestStub);
        classUnderTest.run();

    }
}