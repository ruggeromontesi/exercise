package lt.danske.exercise.exceptions;

import java.io.Serial;

public class InvalidInputException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    //public static final String message = "Account with id %s was not found.";

    public InvalidInputException(String message) {
        super(message);
    }
}
