package lt.danske.exercise.controller;

import lt.danske.exercise.domain.BankAccount;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @PostMapping("/create")
    public ResponseEntity<BankAccount> createAccount(@RequestBody CreateAccountDto account) {

        return null;
    }
}
