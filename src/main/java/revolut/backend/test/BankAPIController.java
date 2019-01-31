package revolut.backend.test;

import revolut.backend.test.routes.RoutingContext;

public interface BankAPIController {
    void createTransfer(RoutingContext context);

    void createAccount(RoutingContext context);

    void getAccount(RoutingContext context);

}
