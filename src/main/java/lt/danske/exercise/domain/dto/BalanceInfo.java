package lt.danske.exercise.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lt.danske.exercise.domain.Currency;

@Builder
@Getter
public class BalanceInfo {
    @Schema(name = "Amount ", example = "1200", requiredMode = Schema.RequiredMode.REQUIRED)
    Double amount;
    @Schema(name = "Currency", example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED)
    Currency currency;
}
