package lt.danske.exercise.integration;

import lombok.RequiredArgsConstructor;
import lt.danske.exercise.controller.CreateAccountDto;
import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.BankAccount;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTests {

    private static final String HTTP_LOCALHOST_8080_CREATE = "http://localhost:8080/create";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;


    @BeforeEach
    public void cleanUp() {
        //accountRepository.deleteAll();
    }


    @Test
    void shouldCreateAccount() {
        List<BankAccount> a = accountRepository.findByUserId(1L);
        RestTemplate restTemplate = new RestTemplate();
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .accountType(AccountType.SAVING)
                .userId(1L)
                .build();

        ResponseEntity<BankAccount> response = restTemplate.postForEntity(HTTP_LOCALHOST_8080_CREATE, new HttpEntity<>(createAccountDto), BankAccount.class);
        List<BankAccount> accounts = accountRepository.findByUserId(1L);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(accounts).hasSize(1),
                () -> assertThat(accounts.stream().findFirst().map(BankAccount::getType).orElseThrow()).isEqualTo(AccountType.SAVING)
        );

    }
}
