package lt.danske.exercise.repository;

import lt.danske.exercise.domain.entity.AccountType;
import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.TransactionStatus;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Customer;
import lt.danske.exercise.domain.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindByAccountId() {
        Customer customer = entityManager.find(Customer.class, 1L);
        Account account = Account.builder()
                .currency(Currency.EUR)
                .type(AccountType.SAVING)
                .customer(customer)
                .build();
        Account savedAccount = entityManager.persist(account);
        Transaction transaction1 = Transaction.builder()
                .account(savedAccount)
                .type(TransactionType.DEPOSIT)
                .amount(10.0)
                .status(TransactionStatus.SUCCESS)
                .build();
        Transaction transaction2 = Transaction.builder()
                .account(savedAccount)
                .type(TransactionType.WITHDRAWAL)
                .amount(11.0)
                .status(TransactionStatus.SUCCESS)
                .build();
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);

        List<Transaction> retrievedTransactions = transactionRepository.findByAccountId(savedAccount.getId());

        assertAll(
                () -> assertThat(retrievedTransactions).hasSize(2),
                () -> {
                    assert retrievedTransactions != null;
                    assertThat(retrievedTransactions.get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
                },
                () -> {
                    assert retrievedTransactions != null;
                    assertThat(retrievedTransactions.get(1).getType()).isEqualTo(TransactionType.WITHDRAWAL);
                }
        );
    }

    @Test
    void shouldNotFindByAccountId() {
        Customer customer = entityManager.find(Customer.class, 2L);
        Account account = Account.builder()
                .currency(Currency.EUR)
                .type(AccountType.SAVING)
                .customer(customer)
                .build();
        Account savedAccount = entityManager.persist(account);

        assertThat(transactionRepository.findByAccountId(savedAccount.getId())).isEmpty();
    }
}