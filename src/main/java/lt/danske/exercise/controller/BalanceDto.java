package lt.danske.exercise.controller;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class BalanceDto {
    double amount;
    Currency currency;

    @Override
    public String toString() {
        return "BalanceDto{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
