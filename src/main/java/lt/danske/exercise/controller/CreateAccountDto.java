package lt.danske.exercise.controller;


import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CreateAccountDto {
    long userId;
    String accountType;
}
