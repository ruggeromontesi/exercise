package lt.danske.exercise.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lt.danske.exercise.domain.Currency;
import lt.danske.exercise.domain.entity.AccountType;
import org.springframework.validation.annotation.Validated;

@Builder
@Getter
@Validated
public class CreateAccountRequest {
    @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long userId;
    @Schema(example = "SAVING", requiredMode = Schema.RequiredMode.REQUIRED)
    AccountType accountType;
    @Schema(example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED)
    Currency currency;
}