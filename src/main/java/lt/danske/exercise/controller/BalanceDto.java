package lt.danske.exercise.controller;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class BalanceDto {
    @NonNull
    Double amount;
    Currency currency;

    @Override
    public String toString() {
        return "BalanceDto{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
