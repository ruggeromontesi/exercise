package lt.danske.exercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public static final String ROOT = "/account";
    public static final String CREATE = "/create";
    public static final String DO_TRANSACTION = "/do/transaction";
    public static final String GET_BALANCE = "/get/balance/{accountId}";
    public static final String GET_TRANSACTIONS = "/get/transactions/{accountId}";
    private final AccountManagementUseCase accountManager;

    @Operation(summary = "Create an account for a customer passing customer_id")
    @PostMapping(value = CREATE)
    public ResponseEntity<Account> createAccount(@RequestBody @Valid CreateAccountDto account) {
        Account createdAccount = accountManager.createAccount(account);
        return ResponseEntity.ok().body(createdAccount);
    }

    @Operation(summary = "Perform a DEPOSIT/WITHDRAWAL on account given account id")
    @PostMapping(value = DO_TRANSACTION)
    public ResponseEntity<Transaction> executeTransaction(@RequestBody RequestTransaction transaction) {
        Transaction a = accountManager.executeTransaction(transaction);
        return ResponseEntity.ok(a);
    }

    @Operation(summary = "Read account balance")
    @GetMapping(GET_BALANCE)
    public ResponseEntity<BalanceDto> getBalance(@Parameter(description = "id of the account for which balance is required")
                                                 @PathVariable("accountId") long accountId) {
        BalanceDto balance = accountManager.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "List the last 10 transactions")
    @GetMapping(GET_TRANSACTIONS)
    public ResponseEntity<List<Transaction>> getLastTenTransactions(
            @Parameter(description = "id of the account for which transaction list is required")
            @PathVariable("accountId") long accountId) {
        List<Transaction> transactions = accountManager.getRecentTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }
}
