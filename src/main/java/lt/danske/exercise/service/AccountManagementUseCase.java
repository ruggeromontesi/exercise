package lt.danske.exercise.service;

import lt.danske.exercise.domain.dto.BalanceDto;
import lt.danske.exercise.domain.dto.CreateAccountRequest;
import lt.danske.exercise.domain.dto.RequestTransaction;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Transaction;

import java.util.List;

public interface AccountManagementUseCase {
    Account createAccount(CreateAccountRequest accountDto);
    Transaction executeTransaction(RequestTransaction transactionDto);
    BalanceDto getBalance(long accountId);
    List<Transaction> getRecentTransactions(long accountId);
}
