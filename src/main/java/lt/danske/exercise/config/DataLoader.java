package lt.danske.exercise.config;

import lt.danske.exercise.controller.Currency;
import lt.danske.exercise.controller.TransactionStatus;
import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.BankUser;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DataLoader(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        BankUser user = BankUser.builder()
                .username("ruggero")
                .build();
        userRepository.save(user);

        BankAccount account = BankAccount.builder()
                .bankUser(user)
                .type(AccountType.SAVING)
                .currency(Currency.EUR)
                .build();
        accountRepository.save(account);

        Transaction t1 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t2 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t3 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t4 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t5 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t6 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t7 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t8 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t9 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t10 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        transactionRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10));

        Transaction t11 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(1.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t12 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(2.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        transactionRepository.saveAll(List.of(t11, t12));
    }
}
