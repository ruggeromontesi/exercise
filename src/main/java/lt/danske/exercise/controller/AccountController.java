package lt.danske.exercise.controller;

import lombok.RequiredArgsConstructor;
import lt.danske.exercise.domain.BankAccount;
import lt.danske.exercise.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;

    @PostMapping(value = "/create")
    public ResponseEntity<BankAccount> createAccount(@RequestBody CreateAccountDto account) {
        BankAccount createdAccount = service.createAccount(account);
        return ResponseEntity.ok().body(createdAccount);
    }
}
