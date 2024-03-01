package lt.danske.exercise.service;

import lombok.RequiredArgsConstructor;
import lt.danske.exercise.controller.CreateAccountDto;
import lt.danske.exercise.domain.BankAccount;
import lt.danske.exercise.domain.BankUser;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public BankAccount createAccount(CreateAccountDto accountDto) {
        BankUser user = userRepository.findById(accountDto.getUserId()).orElseThrow();
        BankAccount account = BankAccount.builder()
                .bankUser(user)
                .type(accountDto.getAccountType())
                .build();

       var savedAccount =  accountRepository.save(account);
       return savedAccount;
    }

}
