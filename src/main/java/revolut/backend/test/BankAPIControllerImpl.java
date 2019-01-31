package revolut.backend.test;

import revolut.backend.test.banking.Account;
import revolut.backend.test.banking.AccountService;
import revolut.backend.test.banking.TransferService;
import revolut.backend.test.banking.impl.AccountServiceImpl;
import revolut.backend.test.banking.impl.TransferServiceImpl;
import revolut.backend.test.exceptions.AccountAlreadyBlocked;
import revolut.backend.test.exceptions.AccountNotFoundException;
import revolut.backend.test.exceptions.InsufficientFundsException;
import revolut.backend.test.exceptions.TransactionErrorException;
import revolut.backend.test.request.TransferRequest;
import revolut.backend.test.response.APIResponseCode;
import revolut.backend.test.response.BaseResponse;
import revolut.backend.test.routes.RoutingContext;
import revolut.backend.test.utils.HttpUtils;

public class BankAPIControllerImpl implements BankAPIController {
    private final TransferService service;
    private final AccountService accountService;

    public BankAPIControllerImpl() {
        service = new TransferServiceImpl();
        accountService = new AccountServiceImpl();
    }


    @Override
    public void createTransfer(RoutingContext context) {
        TransferRequest request = context.requestData().postBodyAs(TransferRequest.class);
        try {
            Account payer = accountService.findAccount(request.getPayerId());
            Account recipient = accountService.findAccount(request.getRecipientId());
            service.transfer(payer, recipient, request.getAmount());
            HttpUtils.writeOkResponse(context.channelContext());
        } catch (AccountAlreadyBlocked e) {
            HttpUtils.writeForbidden(context.channelContext(), APIResponseCode.ACCOUNT_ALREADY_BLOCKED);
        } catch (InsufficientFundsException e) {
            HttpUtils.writeBadRequest(context.channelContext(), APIResponseCode.INSUFFICIENT_FUNDS);
        } catch (TransactionErrorException e) {
            HttpUtils.writeInternalServerError(context.channelContext(), APIResponseCode.TRANSACTION_ERROR);
        } catch (AccountNotFoundException e) {
            HttpUtils.writeNotFoundResponse(context.channelContext(), APIResponseCode.NO_ACCOUNT);
        }

    }

    @Override
    public void createAccount(RoutingContext context) {
        accountService.addAccount(context.requestData().postBodyAs(Account.class));
        HttpUtils.writeOkResponse(context.channelContext());
    }

    @Override
    public void getAccount(RoutingContext context) {
        Long accountId = Long.valueOf(context.requestData().param("accountId"));
        try {
            Account account = accountService.findAccount(accountId);
            HttpUtils.writeOkResponse(context.channelContext(), BaseResponse.create(account));
        } catch (AccountNotFoundException e) {
            HttpUtils.writeNotFoundResponse(context.channelContext(), APIResponseCode.NO_ACCOUNT);
        }
    }
}
