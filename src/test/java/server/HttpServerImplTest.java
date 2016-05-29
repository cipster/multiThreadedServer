package server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class HttpServerImplTest {

    String customFileDirectory = "customFileDirectory";
    private HttpServerImpl classUnderTest;

    @Before
    public void setUp() throws Exception {
        classUnderTest = new HttpServerImpl(0, customFileDirectory);
    }

    @Test
    public void testWithRandomAvailablePort() throws Exception {
        int currentPort = classUnderTest.getPort();

        assertThat("Port is not zero", currentPort, not(0));
    }

    @Test
    public void testWithGivenPort() throws Exception {
        int customPort = 39456;
        classUnderTest = new HttpServerImpl(customPort, customFileDirectory);
        int currentPort = classUnderTest.getPort();

        assertThat("Port is custom port value", currentPort, is(customPort));
    }

    @Test
    public void testWithDefaultFileDirectory() throws Exception {
        classUnderTest = new HttpServerImpl(0, null);

        Path expectedPath = Paths.get(System.getProperty("user.home"), "httpServerFiles");
        Path actualPath = classUnderTest.getFileDirectory();

        assertThat("Default file directory", actualPath, is(expectedPath));

    }

    @Test
    public void testWithGivenFileDirectory() throws Exception {
        File tempDir = new File(customFileDirectory);
        boolean tempDirCreated = tempDir.createNewFile();
        if (!tempDirCreated) {
            Logger.getAnonymousLogger().severe("Please investigate access rights");
        }

        Path expectedPath = tempDir.toPath();
        Path actualPath = classUnderTest.getFileDirectory();


        assertThat("Default file directory", actualPath, is(expectedPath));
        boolean deleted = tempDir.delete();

        if (!deleted) {
            tempDir.deleteOnExit();
        }

    }
}