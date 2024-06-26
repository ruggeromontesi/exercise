package lt.danske.exercise.integration;

import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.TransactionStatus;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.dto.BalanceInfo;
import lt.danske.exercise.domain.dto.CreateAccountRequest;
import lt.danske.exercise.domain.dto.RequestTransaction;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.AccountType;
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

import static lt.danske.exercise.controller.AccountController.CREATE;
import static lt.danske.exercise.controller.AccountController.DO_TRANSACTION;
import static lt.danske.exercise.controller.AccountController.GET_BALANCE;
import static lt.danske.exercise.controller.AccountController.GET_TRANSACTIONS;
import static lt.danske.exercise.controller.AccountController.ROOT;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_DEPOSIT;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_WITHDRAWAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class IntegrationTests {
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
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .accountType(AccountType.SAVING)
                .userId(2L)
                .build();
        HttpEntity<CreateAccountRequest> httpEntity = new HttpEntity<>(createAccountRequest);

        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(LOCALHOST_8080 + ROOT + CREATE,
                httpEntity, Account.class));
    }

    @Test
    void shouldCreateAccount() {
        RestTemplate restTemplate = new RestTemplate();
        CreateAccountRequest createAccountRequest = getAccountDto();

        ResponseEntity<Account> response = restTemplate.postForEntity(LOCALHOST_8080 + ROOT + CREATE,
                new HttpEntity<>(createAccountRequest), Account.class);
        List<Account> accounts = accountRepository.findByCustomerId(1L);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(accounts).hasSize(1),
                () -> {
                    assert accounts != null;
                    assertThat(accounts.stream().findFirst().map(Account::getType).orElseThrow()).isEqualTo(AccountType.SAVING);
                }
        );
    }

    private static CreateAccountRequest getAccountDto() {
        return CreateAccountRequest.builder()
                .accountType(AccountType.SAVING)
                .currency(Currency.EUR)
                .userId(1L)
                .build();
    }

    @Test
    void should_performDeposit() {
        Account createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        RequestTransaction transactionDto = RequestTransaction.builder()
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
    void should_notPerformTransactionWhenAmountNegative() {
        Account createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        RequestTransaction transaction = RequestTransaction.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.DEPOSIT)
                .amount(-1 * AMOUNT_DEPOSIT)
                .build();
        HttpEntity<RequestTransaction> httpEntity = new HttpEntity<>(transaction);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                HttpMethod.POST, httpEntity, Transaction.class));

    }

    @Test
    void should_notPerformWithdrawal_whenBalanceNotEnough() {
        Account createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        Transaction transaction = Transaction.builder()
                .account(createdAccount)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.saveAndFlush(transaction);
        RequestTransaction failingWithdrawal = RequestTransaction.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAWAL)
                .amount(10 * AMOUNT_WITHDRAWAL)
                .build();
        ResponseEntity<Transaction> response = restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(failingWithdrawal), Transaction.class);
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(accountManager.getBalance(createdAccount.getId()).getAmount()).isEqualTo(AMOUNT_DEPOSIT),
                () -> assertThat(Objects.requireNonNull(response.getBody()).getStatus())
                        .isEqualTo(TransactionStatus.FAILURE_NOT_SUFFICIENT_FUNDS)
        );
    }

    @Test
    void should_performWithdrawal_whenBalanceIsEnough() {
        Account createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        Transaction transaction = Transaction.builder()
                .account(createdAccount)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.saveAndFlush(transaction);
        RequestTransaction successfulWithdrawal = RequestTransaction.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAWAL)
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
        Account createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        RequestTransaction deposit = RequestTransaction.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.DEPOSIT)
                .amount(AMOUNT_DEPOSIT)
                .build();

        IntStream.range(0, TIMES).forEach(i -> restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(deposit), Transaction.class));
        RequestTransaction failingWithdrawal = RequestTransaction.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAWAL)
                .amount((TIMES + 1) * AMOUNT_DEPOSIT)
                .build();
        restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION, new HttpEntity<>(failingWithdrawal), Transaction.class);
        ResponseEntity<BalanceInfo> response = restTemplate.exchange(LOCALHOST_8080 + ROOT + GET_BALANCE,
                HttpMethod.GET, new HttpEntity<>(null), BalanceInfo.class, createdAccount.getId());
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(Objects.requireNonNull(response.getBody()).getAmount()).isEqualTo(TIMES * AMOUNT_DEPOSIT)
        );
    }

    @Test
    void should_getTransactions() {
        Account createdAccount = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        RequestTransaction deposit = RequestTransaction.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.DEPOSIT)
                .amount(AMOUNT_DEPOSIT)
                .build();

        IntStream.range(0, TIMES).forEach(i -> restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION,
                new HttpEntity<>(deposit), Transaction.class));
        RequestTransaction failingWithdrawal = RequestTransaction.builder()
                .accountId(createdAccount.getId())
                .type(TransactionType.WITHDRAWAL)
                .amount(AMOUNT_WITHDRAWAL)
                .build();
        restTemplate.postForEntity(LOCALHOST_8080 + ROOT + DO_TRANSACTION, new HttpEntity<>(failingWithdrawal), Transaction.class);
        ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                LOCALHOST_8080 + ROOT + GET_TRANSACTIONS,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                createdAccount.getId()
        );

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(Objects.requireNonNull(response.getBody()).get(0).getType()).isEqualTo(TransactionType.WITHDRAWAL)
        );

    }
}
