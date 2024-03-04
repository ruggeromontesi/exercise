package lt.danske.exercise.domain.dto;

import lombok.Builder;
import lombok.Value;
import lt.danske.exercise.domain.TransactionType;

@Builder
@Value
public class RequestTransaction {
    Long accountId;
    Double amount;
    TransactionType type;
}
