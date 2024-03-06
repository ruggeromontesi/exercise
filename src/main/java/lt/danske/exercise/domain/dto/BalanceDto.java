package lt.danske.exercise.domain.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lt.danske.exercise.domain.Currency;

@Builder
@Value
public class BalanceDto {
    @NonNull
    Double amount;
    @NonNull
    Currency currency;
}
