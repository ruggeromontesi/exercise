package lt.danske.exercise.domain.dto;

import lombok.Builder;
import lombok.Value;
import lt.danske.exercise.domain.TransactionType;

@Builder
@Value
public class TransactionDto {
    Long accountId;
    Double amount;
    TransactionType type;
}
