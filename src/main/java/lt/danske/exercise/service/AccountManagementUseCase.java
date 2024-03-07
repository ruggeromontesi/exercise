package lt.danske.exercise.service;

import lt.danske.exercise.domain.dto.BalanceInfo;
import lt.danske.exercise.domain.dto.CreateAccountRequest;
import lt.danske.exercise.domain.dto.RequestTransaction;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Transaction;

import java.util.List;

public interface AccountManagementUseCase {
    Account createAccount(CreateAccountRequest accountDto);
    Transaction executeTransaction(RequestTransaction transactionDto);
    BalanceInfo getBalance(long accountId);
    List<Transaction> getRecentTransactions(long accountId);
}
