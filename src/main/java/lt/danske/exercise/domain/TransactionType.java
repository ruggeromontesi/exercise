package lt.danske.exercise.domain;

import lombok.Getter;

@Getter
public enum TransactionType {
    DEPOSIT(1), WITHDRAWAL(-1);

    TransactionType(int multiplier) {
        this.multiplier = multiplier;
    }

    private final int multiplier;
}
