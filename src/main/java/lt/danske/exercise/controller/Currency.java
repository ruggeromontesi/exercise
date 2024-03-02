package lt.danske.exercise.controller;

import lombok.Getter;

@Getter
public enum Currency {
    EUR("€");
    private String symbol;
    Currency(String symbol) {
        this.symbol = symbol;
    }
}
