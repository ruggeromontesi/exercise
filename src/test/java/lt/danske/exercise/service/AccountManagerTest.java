package lt.danske.exercise.service;

import lt.danske.exercise.domain.entity.AccountType;
import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.TransactionStatus;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.dto.BalanceDto;
import lt.danske.exercise.domain.dto.CreateAccountRequest;
import lt.danske.exercise.domain.dto.RequestTransaction;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Customer;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.exceptions.AccountNotFoundException;
import lt.danske.exercise.exceptions.InvalidInputException;
import lt.danske.exercise.exceptions.UserNotFoundException;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static lt.danske.exercise.exceptions.AccountNotFoundException.ACCOUNT_NOT_FOUND;
import static lt.danske.exercise.exceptions.UserNotFoundException.USER_NOT_FOUND;
import static lt.danske.exercise.helper.TestHelper.ACCOUNT_ID;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_DEPOSIT;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_WITHDRAWAL;
import static lt.danske.exercise.helper.TestHelper.USERNAME;
import static lt.danske.exercise.helper.TestHelper.USER_ID;
import static lt.danske.exercise.helper.TestHelper.getAllSuccessfulTransactions;
import static lt.danske.exercise.helper.TestHelper.getDeposit;
import static lt.danske.exercise.helper.TestHelper.getSuccessfulAndUnsuccessfulTransactions;
import static lt.danske.exercise.service.AccountManager.MISSING_ACCOUNT_TYPE;
import static lt.danske.exercise.service.AccountManager.MISSING_CURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountManagerTest {
    @InjectMocks
    private AccountManager accountManager;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Captor
    private ArgumentCaptor<Transaction> transactionArgumentCaptor;

    @Test
    void should_createAccount_whenUserExist_and_currencyAndTypeAreProvided() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .accountType(AccountType.SAVING)
                .userId(USER_ID)
                .currency(Currency.EUR)
                .build();
        Customer customer = Customer.builder()
                .id(USER_ID)
                .username(USERNAME)
                .accounts(List.of())
                .build();
        when(customerRepository.findById(USER_ID)).thenReturn(Optional.of(customer));
        Account createdAccount = Account.builder()
                .customer(customer)
                .type(AccountType.SAVING)
                .currency(Currency.EUR)
                .build();

        accountManager.createAccount(createAccountRequest);
        verify(accountRepository, times(1)).saveAndFlush(createdAccount);
    }

    @Test
    void shouldThrow_whenAccountTypeIsMissing() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .userId(USER_ID)
                .currency(Currency.EUR)
                .build();
        Exception e = assertThrows(InvalidInputException.class, () -> accountManager.createAccount(createAccountRequest));
        assertThat(e.getMessage()).isEqualTo(MISSING_ACCOUNT_TYPE);
        verifyNoInteractions(customerRepository);
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldThrow_whenCurrencyIsMissing() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .userId(USER_ID)
                .accountType(AccountType.SAVING)
                .build();
        Exception e = assertThrows(InvalidInputException.class, () -> accountManager.createAccount(createAccountRequest));
        assertThat(e.getMessage()).isEqualTo(MISSING_CURRENCY);
        verifyNoInteractions(customerRepository);
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldThrow_whenUSerNotFound() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .accountType(AccountType.SAVING)
                .userId(USER_ID)
                .currency(Currency.EUR)
                .build();
        when(customerRepository.findById(USER_ID)).thenReturn(Optional.empty());
        Exception e = assertThrows(UserNotFoundException.class, () -> accountManager.createAccount(createAccountRequest));
        assertThat(e.getMessage()).isEqualTo(String.format(USER_NOT_FOUND, USER_ID));
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void should_executeTransaction_whenAccountExists_andBalanceIsEnough() {
        RequestTransaction transaction = RequestTransaction.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .build();
        Account account = getSavingAccount();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        accountManager.executeTransaction(transaction);

        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());
        Transaction capturedTransaction = transactionArgumentCaptor.getValue();
        assertAll(
                () -> assertThat(capturedTransaction.getAccount()).isEqualTo(account),
                () -> assertThat(capturedTransaction.getType()).isEqualTo(TransactionType.DEPOSIT),
                () -> assertThat(capturedTransaction.getAmount()).isEqualTo(AMOUNT_DEPOSIT),
                () -> assertThat(capturedTransaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS)
        );
    }

    private static Account getSavingAccount() {
        return Account.builder()
                .id(ACCOUNT_ID)
                .currency(Currency.EUR)
                .type(AccountType.SAVING)
                .build();
    }

    private static Account getCurrentAccount() {
        return Account.builder()
                .id(ACCOUNT_ID)
                .currency(Currency.EUR)
                .type(AccountType.CURRENT)
                .build();
    }

    @Test
    void shouldNot_executeTransaction_whenSavingAccountExists_andBalanceIsNotEnough() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(getSavingAccount()));
        when(transactionRepository.findByAccountId(ACCOUNT_ID)).thenReturn(List.of(getDeposit(0.5 * AMOUNT_DEPOSIT)));
        RequestTransaction transactionDto = RequestTransaction.builder()
                .accountId(ACCOUNT_ID)
                .amount(2 * AMOUNT_WITHDRAWAL)
                .type(TransactionType.WITHDRAWAL)
                .build();

        accountManager.executeTransaction(transactionDto);

        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());
        Transaction capturedTransaction = transactionArgumentCaptor.getValue();
        assertAll(
                () -> assertThat(capturedTransaction.getType()).isEqualTo(TransactionType.WITHDRAWAL),
                () -> assertThat(capturedTransaction.getAmount()).isEqualTo(2 * AMOUNT_WITHDRAWAL),
                () -> assertThat(capturedTransaction.getStatus()).isEqualTo(TransactionStatus.FAILURE_NOT_ENOUGH_BALANCE)
        );
    }

    @Test
    void should_executeTransaction_whenCurrentAccountExists_andBalanceIsNotEnough() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(getCurrentAccount()));
        RequestTransaction transactionDto = RequestTransaction.builder()
                .accountId(ACCOUNT_ID)
                .amount(2 * AMOUNT_WITHDRAWAL)
                .type(TransactionType.WITHDRAWAL)
                .build();

        accountManager.executeTransaction(transactionDto);

        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());
        Transaction capturedTransaction = transactionArgumentCaptor.getValue();
        assertAll(
                () -> assertThat(capturedTransaction.getType()).isEqualTo(TransactionType.WITHDRAWAL),
                () -> assertThat(capturedTransaction.getAmount()).isEqualTo(2 * AMOUNT_WITHDRAWAL),
                () -> assertThat(capturedTransaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS)
        );
    }

    @Test
    void shouldThrowException_whenAccountDoesNotExist() {
        RequestTransaction transaction = RequestTransaction.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .build();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        Exception e = assertThrows(AccountNotFoundException.class, () -> accountManager.executeTransaction(transaction));
        assertThat(e.getMessage()).isEqualTo(String.format(ACCOUNT_NOT_FOUND, ACCOUNT_ID));
    }

    @Test
    void shouldThrow_whenAmountNegative() {
        RequestTransaction transaction = RequestTransaction.builder()
                .accountId(ACCOUNT_ID)
                .amount(-1 * AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .build();
        Exception e = assertThrows(InvalidInputException.class, () -> accountManager.executeTransaction(transaction));
        assertThat(e.getMessage()).isEqualTo(AccountManager.AMOUNT_MUST_BE_POSITIVE);
    }

    @Test
    void should_getBalance_whenAccountExists() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(getSavingAccount()));
        when(transactionRepository.findByAccountId(ACCOUNT_ID)).thenReturn(getAllSuccessfulTransactions(21));

        BalanceDto balance = accountManager.getBalance(ACCOUNT_ID);
        assertThat(balance).satisfies(
                b -> assertThat(b.getAmount()).isEqualTo(20 * AMOUNT_DEPOSIT - AMOUNT_WITHDRAWAL),
                b -> assertThat(b.getCurrency()).isEqualTo(Currency.EUR)
        );
    }

    @Test
    void should_getBalance_andIgnoreFailedTransactions_whenAccountExists() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(getSavingAccount()));
        when(transactionRepository.findByAccountId(ACCOUNT_ID)).thenReturn(getSuccessfulAndUnsuccessfulTransactions());

        BalanceDto balance = accountManager.getBalance(ACCOUNT_ID);

        assertThat(balance).satisfies(
                b -> assertThat(b.getAmount()).isEqualTo(AMOUNT_DEPOSIT),
                b -> assertThat(b.getCurrency()).isEqualTo(Currency.EUR)
        );
    }

    @Test
    void shouldThrow_whenAccountDoesNotExist() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        Exception e = assertThrows(AccountNotFoundException.class, () -> accountManager.getBalance(ACCOUNT_ID));

        assertThat(e.getMessage()).isEqualTo(String.format(ACCOUNT_NOT_FOUND, ACCOUNT_ID));
    }

    @Test
    void getRecentTransactions() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(getSavingAccount()));
        when(transactionRepository.findByAccountId(ACCOUNT_ID)).thenReturn(getAllSuccessfulTransactions(21));

        List<Transaction> transactions = accountManager.getRecentTransactions(ACCOUNT_ID);

        Transaction lastTransaction = transactions.stream().findFirst().orElseThrow();
        assertAll(
                () -> assertThat(transactions).hasSize(AccountManager.COUNT_OF_MOST_RECENT_TRANSACTIONS),
                () -> assertThat(lastTransaction.getType()).isEqualTo(TransactionType.WITHDRAWAL)
        );
    }
}