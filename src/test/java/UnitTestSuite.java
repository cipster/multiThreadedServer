import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import server.HttpServerImplTest;
import server.ResponseDispatcherImplTest;
import server.SocketConnectionTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HttpServerImplTest.class,
        ResponseDispatcherImplTest.class,
        SocketConnectionTest.class
})
public class UnitTestSuite {
}
