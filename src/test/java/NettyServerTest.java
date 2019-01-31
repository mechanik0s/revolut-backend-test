import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import revolut.backend.test.NettyServer;
import revolut.backend.test.routes.Router;
import revolut.backend.test.routes.impl.RouterImpl;


/**
 * just test server starting and shutdown correctness
 */
public class NettyServerTest {

    NettyServer nettyServer;

    @BeforeClass
    public void setupServer() {
        Router router = new RouterImpl();
        router.get("/test", System.out::println);
        nettyServer = new NettyServer(8080, router);
        nettyServer.start();
    }

    @AfterClass
    public void stopServer() {
        nettyServer.shutdown();
    }

    @Test
    public void Test() {
        System.out.println("TESSSSSSSSSST");
    }
}
