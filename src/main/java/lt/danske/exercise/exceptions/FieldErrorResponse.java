package lt.danske.exercise.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class FieldErrorResponse {
    private String fieldName;
    private String errorMessage;
}