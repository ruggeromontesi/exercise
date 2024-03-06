package lt.danske.exercise.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lt.danske.exercise.domain.entity.AccountType;
import lt.danske.exercise.domain.Currency;
import org.springframework.validation.annotation.Validated;

@Builder
@Value
@Validated
public class CreateAccountRequest {
    @NotNull
    Long userId;
    @NotNull
    AccountType accountType;
    @NotNull
    Currency currency;
}
