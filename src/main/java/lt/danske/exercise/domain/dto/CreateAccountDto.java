package lt.danske.exercise.domain.dto;

import lombok.Builder;
import lombok.Value;
import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.AccountType;
import org.springframework.validation.annotation.Validated;

@Builder
@Value
@Validated
public class CreateAccountDto {
    long userId;

    AccountType accountType;
    Currency currency;
}
