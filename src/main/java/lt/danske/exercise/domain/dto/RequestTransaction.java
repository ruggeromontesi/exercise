package lt.danske.exercise.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lt.danske.exercise.domain.TransactionType;
import org.springframework.validation.annotation.Validated;

@Builder
@Getter
@Validated
public class RequestTransaction {
    @Schema(name = "Account ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long accountId;
    @Schema(name = "Amount", example = "100.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 0)
    Double amount;
    @Schema(name = "Transaction type", example = "DEPOSIT", requiredMode = Schema.RequiredMode.REQUIRED)
    TransactionType type;
}
