package lt.danske.exercise.integration;

import lt.danske.exercise.controller.CreateAccountDto;
import lt.danske.exercise.controller.Currency;
import lt.danske.exercise.controller.TransactionDto;
import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.service.AccountManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTests {
    private static final String HTTP_LOCALHOST_8080_CREATE = "http://localhost:8080/create";
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

        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForEntity(HTTP_LOCALHOST_8080_CREATE, new HttpEntity<>(createAccountDto), BankAccount.class));
    }

    @Test
    void shouldCreateAccount() {
        RestTemplate restTemplate = new RestTemplate();
        CreateAccountDto createAccountDto = getAccountDto();

        ResponseEntity<BankAccount> response = restTemplate.postForEntity(HTTP_LOCALHOST_8080_CREATE, new HttpEntity<>(createAccountDto), BankAccount.class);
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
    void should_performTransaction() {
        var a = accountManager.createAccount(getAccountDto());
        RestTemplate restTemplate = new RestTemplate();
        TransactionDto transactionDto = TransactionDto.builder()
                .accountId(a.getId())
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/performtransaction", new HttpEntity<>(transactionDto), String.class);
        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(accountManager.getBalance(a.getId()).getAmount()).isEqualTo(100.0)
        );
    }
}
