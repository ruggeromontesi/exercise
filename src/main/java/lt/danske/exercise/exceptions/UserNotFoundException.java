package lt.danske.exercise.exceptions;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final String USER_NOT_FOUND = "User with id %s was not found.";

    public UserNotFoundException(long userId) {
        super(String.format(USER_NOT_FOUND, userId));
    }
}