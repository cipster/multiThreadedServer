import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import server.HttpServerImplTest;
import server.ResponseDispatcherImplTest;
import server.SocketConnectionTest;
import server.request.HttpRequestParserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HttpRequestParserTest.class,
        HttpServerImplTest.class,
        ResponseDispatcherImplTest.class,
        SocketConnectionTest.class
})
public class UnitTestSuite {
}
