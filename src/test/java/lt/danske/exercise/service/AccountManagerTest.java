package lt.danske.exercise.service;

import lt.danske.exercise.controller.CreateAccountDto;
import lt.danske.exercise.controller.Currency;
import lt.danske.exercise.controller.TransactionDto;
import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.BankUser;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.exceptions.AccountNotFoundException;
import lt.danske.exercise.exceptions.InvalidInputException;
import lt.danske.exercise.exceptions.UserNotFoundException;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static lt.danske.exercise.helper.TestHelper.ACCOUNT_ID;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_DEPOSIT;
import static lt.danske.exercise.helper.TestHelper.USERNAME;
import static lt.danske.exercise.helper.TestHelper.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountManagerTest {
    private static final String MISSING_ACCOUNT_TYPE = "Missing account type";
    private static final String MISSING_CURRENCY = "Missing currency";
    private static final String USER_WAS_NOT_FOUND = "User with id %s was not found.";
    private static final String ACCOUNT_WAS_NOT_FOUND = "Account with id %s was not found.";
    @InjectMocks
    private AccountManager accountManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Captor
    private ArgumentCaptor<Transaction> transactionArgumentCaptor;

    @Test
    void should_createAccount_whenUserExist_and_currencyAndTypeAreProvided() {
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .accountType(AccountType.SAVING)
                .userId(USER_ID)
                .currency(Currency.EUR)
                .build();
        BankUser bankUser = BankUser.builder()
                .id(USER_ID)
                .username(USERNAME)
                .accounts(List.of())
                .build();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(bankUser));
        BankAccount createdBankAccount = BankAccount.builder()
                .bankUser(bankUser)
                .type(AccountType.SAVING)
                .currency(Currency.EUR)
                .build();

        accountManager.createAccount(createAccountDto);
        verify(accountRepository, times(1)).save(createdBankAccount);
    }

    @Test
    void shouldThrow_whenAccountTypeIsMissing() {
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .userId(USER_ID)
                .currency(Currency.EUR)
                .build();
        Exception e = assertThrows(InvalidInputException.class, () -> accountManager.createAccount(createAccountDto));
        assertThat(e.getMessage()).isEqualTo(MISSING_ACCOUNT_TYPE);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldThrow_whenCurrencyIsMissing() {
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .userId(USER_ID)
                .accountType(AccountType.SAVING)
                .build();
        Exception e = assertThrows(InvalidInputException.class, () -> accountManager.createAccount(createAccountDto));
        assertThat(e.getMessage()).isEqualTo(MISSING_CURRENCY);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldThrow_whenUSerNotFound() {
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .accountType(AccountType.SAVING)
                .userId(USER_ID)
                .currency(Currency.EUR)
                .build();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        Exception e = assertThrows(UserNotFoundException.class, () -> accountManager.createAccount(createAccountDto));
        assertThat(e.getMessage()).isEqualTo(String.format(USER_WAS_NOT_FOUND, USER_ID));
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void should_executeTransaction_whenAccountExists() {
        TransactionDto transactionDto = TransactionDto.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .build();
        BankAccount account = BankAccount.builder()
                .id(ACCOUNT_ID)
                .currency(Currency.EUR)
                .type(AccountType.SAVING)
                .build();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        accountManager.executeTransaction(transactionDto);

        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());
        Transaction capturedTransaction = transactionArgumentCaptor.getValue();
        assertAll(
                () -> assertThat(capturedTransaction.getBankAccount()).isEqualTo(account),
                () -> assertThat(capturedTransaction.getType()).isEqualTo(TransactionType.DEPOSIT),
                () -> assertThat(capturedTransaction.getAmount()).isEqualTo(AMOUNT_DEPOSIT)
        );
    }

    @Test
    void shouldThrowException_whenAccountDoesNotExist() {
        TransactionDto transactionDto = TransactionDto.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT_DEPOSIT)
                .type(TransactionType.DEPOSIT)
                .build();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        Exception e = assertThrows(AccountNotFoundException.class, () -> accountManager.executeTransaction(transactionDto));
        assertThat(e.getMessage()).isEqualTo(String.format(ACCOUNT_WAS_NOT_FOUND, ACCOUNT_ID));
    }

    @Test
    void getBalance() {
    }

    @Test
    void getRecentTransactions() {
    }
}