package lt.danske.exercise.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lt.danske.exercise.domain.dto.BalanceDto;
import lt.danske.exercise.domain.dto.CreateAccountDto;
import lt.danske.exercise.domain.dto.RequestTransaction;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.service.AccountManagementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = AccountController.ROOT)
public class AccountController {
    public static final String ROOT = "/account/management";
    public static final String CREATE = "/create";
    public static final String DO_TRANSACTION = "/performtransaction";
    public static final String BALANCE_ACCOUNT_ID = "/balance/{accountId}";
    public static final String TRANSACTIONS_ACCOUNT_ID = "/transactions/{accountId}";
    private final AccountManagementUseCase accountManager;

    @PostMapping(value = CREATE)
    public ResponseEntity<Account> createAccount(@RequestBody @Valid CreateAccountDto account) {
        Account createdAccount = accountManager.createAccount(account);
        return ResponseEntity.ok().body(createdAccount);
    }

    @PostMapping(value = DO_TRANSACTION)
    public ResponseEntity<Transaction> executeTransaction(@RequestBody RequestTransaction transaction) {
        Transaction a = accountManager.executeTransaction(transaction);
        return ResponseEntity.ok(a);
    }

    @GetMapping(BALANCE_ACCOUNT_ID)
    public ResponseEntity<BalanceDto> getBalance(@PathVariable("accountId") long accountId) {
        BalanceDto balance = accountManager.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping(TRANSACTIONS_ACCOUNT_ID)
    public ResponseEntity<List<Transaction>> getLastTenTransactions(@PathVariable("accountId") long accountId) {
        List<Transaction> transactions = accountManager.getRecentTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }
}
