package lt.danske.exercise.exceptions;

import java.io.Serial;

public class AccountNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final String ACCOUNT_NOT_FOUND = "Account with id %s was not found.";

    public AccountNotFoundException(long accountId) {
        super(String.format(ACCOUNT_NOT_FOUND, accountId));
    }
}
