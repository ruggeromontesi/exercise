package lt.danske.exercise.service;

import lt.danske.exercise.controller.BalanceDto;
import lt.danske.exercise.controller.CreateAccountDto;
import lt.danske.exercise.controller.TransactionDto;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.Transaction;

import java.util.List;

public interface AccountManagementUseCase {
    BankAccount createAccount(CreateAccountDto accountDto);
    Transaction executeTransaction(TransactionDto transactionDto);
    BalanceDto getBalance(long accountId);
    List<Transaction> getRecentTransactions(long accountId);
}
