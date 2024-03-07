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
    @Schema(name = "Account ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long userId;
    @Schema(name = "Account type", example = "SAVING", requiredMode = Schema.RequiredMode.REQUIRED)
    AccountType accountType;
    @Schema(name = "Currency", example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED)
    Currency currency;
}