package lt.danske.exercise.domain;

import lombok.Getter;

@Getter
public enum Currency {
    EUR("€");
    private final String symbol;
    Currency(String symbol) {
        this.symbol = symbol;
    }
}
