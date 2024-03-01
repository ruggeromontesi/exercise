package lt.danske.exercise.controller;


import lombok.Builder;
import lombok.Value;
import lt.danske.exercise.domain.AccountType;

@Builder
@Value
public class CreateAccountDto {
    long userId;
    AccountType accountType;
}
