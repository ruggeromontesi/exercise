package lt.danske.exercise.controller;

import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.dto.CreateAccountRequest;
import lt.danske.exercise.domain.dto.RequestTransaction;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.AccountType;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.service.AccountManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static lt.danske.exercise.helper.TestHelper.ACCOUNT_ID;
import static lt.danske.exercise.helper.TestHelper.AMOUNT_DEPOSIT;
import static lt.danske.exercise.helper.TestHelper.USERNAME;
import static lt.danske.exercise.helper.TestHelper.USER_ID;
import static lt.danske.exercise.helper.TestHelper.getAccount;
import static lt.danske.exercise.helper.TestHelper.getDeposit;
import static lt.danske.exercise.helper.TestHelper.getRequestTransaction;
import static lt.danske.exercise.helper.TestHelper.getCreateAccountRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;
    @Mock
    private AccountManager accountManager;

    @Test
    void should_createAccount() {
        CreateAccountRequest request = getCreateAccountRequest();
        when(accountManager.createAccount(request)).thenReturn(getAccount(AccountType.SAVING));

        ResponseEntity<Account> result = accountController.createAccount(request);

        verify(accountManager, times(1)).createAccount(request);
        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(Objects.requireNonNull(result.getBody()).getId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getCustomer().getId())).isEqualTo(USER_ID),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getCustomer().getUsername())).isEqualTo(USERNAME)
        );

    }

    @Test
    void executeTransaction() {
        RequestTransaction requestTransaction = getRequestTransaction(TransactionType.DEPOSIT, AMOUNT_DEPOSIT);
        when(accountManager.executeTransaction(requestTransaction)).thenReturn(getDeposit(AMOUNT_DEPOSIT));

        ResponseEntity<Transaction> response = accountController.executeTransaction(requestTransaction);
        verify(accountManager, times(1)).executeTransaction(requestTransaction);
    }


    @Test
    void getBalance() {
    }

    @Test
    void getLastTenTransactions() {
    }
}