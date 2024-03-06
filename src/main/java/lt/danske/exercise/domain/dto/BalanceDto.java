package lt.danske.exercise.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lt.danske.exercise.domain.Currency;

@Builder
@Getter
public class BalanceDto {
    @NonNull
    Double amount;
    @NonNull
    Currency currency;
}
