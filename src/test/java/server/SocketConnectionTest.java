package server;

import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import server.common.HttpMethod;
import server.request.HttpRequest;
import server.request.HttpRequests;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpRequests.class})
public class SocketConnectionTest {

    @Mock
    private Socket socket;
    @Mock
    private HttpServer httpServer;
    @Mock
    private HttpRequest httpRequest;

    @InjectMocks
    private SocketConnection classUnderTest;
    private String requestString;

    @Before
    public void setUp() throws Exception {
        requestString = FileUtils.readFileToString(new File("."));
        mockStatic(HttpRequests.class);
        when(HttpRequests.parse(any(InputStream.class), any(Path.class))).thenReturn(httpRequest);
        when(httpRequest.getUrl()).thenReturn("/index.html");
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(httpRequest.getHeader()).thenReturn(Maps.newHashMap());
        when(httpRequest.getInputStream()).thenReturn(IOUtils.toInputStream(requestString));
        when(httpServer.getDispatcher()).thenReturn(new ResponseDispatcherImpl(httpServer));

    }

    @Test
    public void testRunWithIOException() throws Exception {
        classUnderTest.run();
    }
}