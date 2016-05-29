package server.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpRequests.class})
public class HttpRequestsTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void mockAStaticMethod() {
        mockStatic(HttpRequests.class);
        HttpRequest expected = null;
        when(HttpRequests.parse(any(InputStream.class), any(Path.class))).thenReturn(expected);

    }
}