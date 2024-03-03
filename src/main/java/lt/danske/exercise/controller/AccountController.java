package lt.danske.exercise.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.service.AccountManagementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountManagementUseCase accountManager;

    @PostMapping(value = "/create")
    public ResponseEntity<BankAccount> createAccount(@RequestBody @Valid CreateAccountDto account) {
        BankAccount createdAccount = accountManager.createAccount(account);
        return ResponseEntity.ok().body(createdAccount);
    }

    @PostMapping(value = "/performtransaction")
    public ResponseEntity<Transaction> executeTransaction(@RequestBody TransactionDto transaction) {
        Transaction a = accountManager.executeTransaction(transaction);
        return ResponseEntity.ok(a);
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<BalanceDto> getBalance(@PathVariable("accountId") long accountId) {
        BalanceDto balance = accountManager.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<List<Transaction>> getLastTenTransactions(@PathVariable("accountId") long accountId) {
        List<Transaction> transactions = accountManager.getRecentTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }
}
