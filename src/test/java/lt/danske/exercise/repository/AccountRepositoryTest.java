package lt.danske.exercise.repository;

import lt.danske.exercise.domain.entity.AccountType;
import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindByCustomerId() {
        Customer customer = entityManager.find(Customer.class, 1L);
        Account account = Account.builder()
                .currency(Currency.EUR)
                .type(AccountType.SAVING)
                .customer(customer)
                .build();
        entityManager.persist(account);

        List<Account> accountsOfTestUser = accountRepository.findByCustomerId(customer.getId());

        Account retrievedAccount = accountsOfTestUser.stream().findFirst().orElseThrow();

        assertAll(
                () -> assertThat(accountsOfTestUser).hasSize(1),
                () -> assertThat(retrievedAccount.getType()).isEqualTo(AccountType.SAVING),
                () -> assertThat(retrievedAccount.getCurrency()).isEqualTo(Currency.EUR),
                () -> assertThat(retrievedAccount.getCustomer().getId()).isEqualTo(customer.getId())
        );
    }

    @Test
    void shouldNotFindByCustomerId() {
        Customer customer = entityManager.find(Customer.class, 11L);
        Account account = Account.builder()
                .currency(Currency.EUR)
                .type(AccountType.SAVING)
                .customer(customer)
                .build();
        entityManager.persist(account);

        assertThat(accountRepository.findByCustomerId(11L)).isEmpty();
    }
}