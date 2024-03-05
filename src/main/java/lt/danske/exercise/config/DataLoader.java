package lt.danske.exercise.config;

import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.TransactionStatus;
import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Customer;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DataLoader(CustomerRepository customerRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Customer user = Customer.builder()
                .username("ruggero")
                .build();
        customerRepository.save(user);

        Account account = Account.builder()
                .customer(user)
                .type(AccountType.SAVING)
                .currency(Currency.EUR)
                .build();
        accountRepository.save(account);

        Transaction t1 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t2 = Transaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t3 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t4 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t5 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t6 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t7 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t8 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t9 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t10 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        transactionRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10));

        Transaction t11 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(1.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction t12 = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(2.0)
                .status(TransactionStatus.SUCCESS)
                .build();

        transactionRepository.saveAll(List.of(t11, t12));
    }
}