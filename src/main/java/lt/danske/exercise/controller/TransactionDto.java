package lt.danske.exercise.controller;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lt.danske.exercise.domain.TransactionType;

@Builder
@Value
public class TransactionDto {
    Long accountId;
    Double amount;
    TransactionType type;
}
