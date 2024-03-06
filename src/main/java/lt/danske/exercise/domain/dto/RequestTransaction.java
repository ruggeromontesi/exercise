package lt.danske.exercise.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lt.danske.exercise.domain.TransactionType;
import org.springframework.validation.annotation.Validated;

@Builder
@Getter
@Validated
public class RequestTransaction {
    @NotNull
    Long accountId;
    @NotNull
    @Min(value = 0)
    Double amount;
    @NotNull
    TransactionType type;
}
