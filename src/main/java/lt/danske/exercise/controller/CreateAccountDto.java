package lt.danske.exercise.controller;


import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lt.danske.exercise.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Builder
@Value
@Validated
public class CreateAccountDto {
    long userId;

    AccountType accountType;
    Currency currency;
}
