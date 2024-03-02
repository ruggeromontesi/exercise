package lt.danske.exercise.domain;

public enum TransactionType {
    DEPOSIT(1), WITHDRAW(-1);

    TransactionType(int multiplier) {
        this.multiplier = multiplier;
    }
    private final int multiplier;

    public int getMultiplier() {
        return this.multiplier;
    }

}
