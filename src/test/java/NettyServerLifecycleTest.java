import org.testng.annotations.Test;
import revolut.backend.test.NettyServer;
import revolut.backend.test.routes.Router;
import revolut.backend.test.routes.impl.RouterImpl;


/**
 * just test server starting and shutdown correctness
 */
public class NettyServerLifecycleTest {

    private NettyServer nettyServer;

    @Test
    public void setupServer() {
        Router router = new RouterImpl();
        nettyServer = new NettyServer(8080, router);
        nettyServer.start();
    }

    @Test(dependsOnMethods = "setupServer")
    public void stopServer() {
        nettyServer.shutdown();
    }


}
