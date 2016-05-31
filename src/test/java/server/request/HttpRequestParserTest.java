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
@PrepareForTest({HttpRequestParser.class})
public class HttpRequestParserTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void mockAStaticMethod() {
        mockStatic(HttpRequestParser.class);
        HttpRequest expected = null;
        when(HttpRequestParser.parse(any(InputStream.class), any(Path.class))).thenReturn(expected);

    }
}