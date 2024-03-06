package lt.danske.exercise.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.entity.AccountType;
import org.springframework.validation.annotation.Validated;

@Builder
@Getter
@Validated
public class CreateAccountRequest {
    @NotNull
    Long userId;
    @NotNull
    AccountType accountType;
    @NotNull
    Currency currency;
}