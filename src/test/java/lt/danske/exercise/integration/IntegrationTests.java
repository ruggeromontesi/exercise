package lt.danske.exercise.integration;

import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.TransactionStatus;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.dto.BalanceDto;
import lt.danske.exercise.domain.dto.CreateAccountDto;
import lt.danske.exercise.domain.dto.TransactionDto;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.service.AccountManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static lt.danske.exercise.controller.AccountController.BALANCE_ACCOUNT_ID;
import static lt.danske.exercise.controller.AccountController.CREATE;
import static lt.danske.exercise.controller.AccountController.DO_TRANSACTION;
import static lt.danske.exercise.controller.AccountController.ROOT;
import static lt.danske.exercise.controller.AccountController.TRANSACTIONS_ACCOUNT_ID;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_DEPOSIT;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_WITHDRAWAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTests {
    private static final String LOCALHOST_8080 = "http://localhost:8080";
    private static final int TIMES = 5;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountManager accountManager;

    @BeforeEach
    public void setup() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void shouldThrow_whenUserNotFound() {
        RestTemplate restTemplate = new RestTemplate();
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .accountType(AccountType.SAVING)
                .userId(2L)
                .build();
        HttpEntity<CreateAccountDto> httpEntity = new HttpEntity<>(createAccountDto);

        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(LOCALHOST_8080 + ROOT + CREATE,
                httpEntity, BankAccount.class));
    }

    @Test
    void shouldCreateAccount() {
        RestTemplate restTemplate = new RestTemplate();
        CreateAccountDto createAccountDto = getAccountDto();

        ResponseEntity<BankAccount> response = restTemplate.postForEntity(LOCALHOST_8080 + ROOT + CREATE,
                new HttpEntity<>(createAccountDto), BankAccount.class);
        List<BankAccount> accounts = accountRepository.findByUserId(1L);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(accounts).hasSize(1),
                () -> assertThat(accounts.stream().findFirst().map(BankAccount::getType).orElseThrow()).isEqualTo(AccountType.SAVING)
        );
    }

    private static CreateAccountDto getAccountDto() {
        return CreateAccountDto.builder()
                .accountType(AccountType.SAVING)
                .currency(Currency.EUR)
                .userId(1L)
                .build();
    }

    @Test
    void should_performDeposit() {
        BankAccount createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        TransactionDto transactionDto = TransactionDto.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.DEPOSIT)
                .amount(AMOUNT_DEPOSIT)
                .build();
        ResponseEntity<Transaction> response = restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(transactionDto), Transaction.class);
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(accountManager.getBalance(createdAccount.getId()).getAmount()).isEqualTo(AMOUNT_DEPOSIT),
                () -> assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(TransactionStatus.SUCCESS)
        );
    }

    @Test
    void should_notPerformWithdrawal_whenBalanceNotEnough() {
        BankAccount createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        Transaction transaction = Transaction.builder()
                .bankAccount(createdAccount)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.saveAndFlush(transaction);
        TransactionDto failingWithdrawal = TransactionDto.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAW)
                .amount(10 * AMOUNT_WITHDRAWAL)
                .build();
        ResponseEntity<Transaction> response = restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(failingWithdrawal), Transaction.class);
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(accountManager.getBalance(createdAccount.getId()).getAmount()).isEqualTo(AMOUNT_DEPOSIT),
                () -> assertThat(Objects.requireNonNull(response.getBody()).getStatus())
                        .isEqualTo(TransactionStatus.FAILURE_NOT_ENOUGH_BALANCE)
        );
    }

    @Test
    void should_performWithdrawal_whenBalanceIsEnough() {
        BankAccount createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        Transaction transaction = Transaction.builder()
                .bankAccount(createdAccount)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.saveAndFlush(transaction);
        TransactionDto successfulWithdrawal = TransactionDto.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAW)
                .amount(AMOUNT_WITHDRAWAL)
                .build();
        ResponseEntity<Transaction> response = restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(successfulWithdrawal), Transaction.class);
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(accountManager.getBalance(createdAccount.getId()).getAmount())
                        .isEqualTo(AMOUNT_DEPOSIT - AMOUNT_WITHDRAWAL),
                () -> assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(TransactionStatus.SUCCESS)
        );
    }

    @Test
    void should_getBalance() {
        BankAccount createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        TransactionDto deposit = TransactionDto.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.DEPOSIT)
                .amount(AMOUNT_DEPOSIT)
                .build();

        IntStream.range(0, TIMES).forEach(i -> restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(deposit), Transaction.class));
        TransactionDto failingWithdrawal = TransactionDto.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAW)
                .amount((TIMES + 1) * AMOUNT_DEPOSIT)
                .build();
        restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION, new HttpEntity<>(failingWithdrawal), Transaction.class);
        ResponseEntity<BalanceDto> response = restTemplate.exchange(LOCALHOST_8080 + ROOT + BALANCE_ACCOUNT_ID,
                HttpMethod.GET, new HttpEntity<>(null), BalanceDto.class, createdAccount.getId());
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(Objects.requireNonNull(response.getBody()).getAmount()).isEqualTo(TIMES * AMOUNT_DEPOSIT)
        );
    }

    @Test
    void should_getTransactions() {
        BankAccount createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        TransactionDto deposit = TransactionDto.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.DEPOSIT)
                .amount(AMOUNT_DEPOSIT)
                .build();

        IntStream.range(0, TIMES).forEach(i -> restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(deposit), Transaction.class));
        TransactionDto failingWithdrawal = TransactionDto.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAW)
                .amount(AMOUNT_WITHDRAWAL)
                .build();
        restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION, new HttpEntity<>(failingWithdrawal), Transaction.class);
        ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                LOCALHOST_8080 + ROOT + TRANSACTIONS_ACCOUNT_ID,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                createdAccount.getId()
        );

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().stream().findFirst().orElseThrow().getType()).isEqualTo(TransactionType.WITHDRAW)
        );

    }
}
