package lt.danske.exercise.controller;

import lt.danske.exercise.domain.dto.CreateAccountRequest;
import lt.danske.exercise.helper.TestHelper;
import lt.danske.exercise.service.AccountManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static lt.danske.exercise.helper.TestHelper.getAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;
    @Mock
    private AccountManager accountManager;

    @Test
    void should_createAccount() {
        CreateAccountRequest request = TestHelper.getCreateAccountRequest();
        when(accountManager.createAccount(request)).thenReturn(getAccount());

        var result = accountController.createAccount(request);

        assertThat(result).isNotNull();

    }

    @Test
    void executeTransaction() {
    }

    @Test
    void getBalance() {
    }

    @Test
    void getLastTenTransactions() {
    }
}