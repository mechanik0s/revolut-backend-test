package revolut.backend.test;

import revolut.backend.test.routes.Router;
import revolut.backend.test.routes.impl.RouterImpl;

public class Main {

    public static void main(String[] args) {
        BankAPIController apiController = new BankAPIControllerImpl();
        Router router = new RouterImpl();
        router
                .post("/account", apiController::createAccount)
                // In true RESTful API request should look like
                // /accounts/{accountId}
                // but for simplicity let's use query-parameter
                .get("/account", apiController::getAccount)
                .post("/transfer", apiController::createTransfer);
        NettyServer nettyServer = new NettyServer(8080, router);
        nettyServer.start();
    }
}

