package lt.danske.exercise.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FieldErrorResponse {
    private String fieldName;
    private String errorMessage;
}