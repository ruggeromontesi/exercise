package lt.danske.exercise.helper;

import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.BankUser;
import lt.danske.exercise.domain.entity.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestHelper {
    public static final double AMOUNT_WITHDRAWAL = 50.0;
    public static long USER_ID = 1001L;
    public static final String USERNAME = "TEST_USERNAME";
    public static final long ACCOUNT_ID = 100001L;
    public static final double AMOUNT_DEPOSIT = 100.0;

    public static List<Transaction> getTransactions(int numberOfTransactions) {
        List<Transaction> deposits = IntStream.range(0, numberOfTransactions - 1).mapToObj(i -> getDeposit()).toList();
        List<Transaction> withdrawal = List.of(getWithdrawal());
        return Stream.of(deposits, withdrawal).flatMap(Collection::stream).toList();
    }

    private static Transaction getWithdrawal() {
        return Transaction.builder()
                .bankAccount(getAccount())
                .type(TransactionType.WITHDRAW)
                .amount(AMOUNT_WITHDRAWAL)
                .build();
    }

    private static Transaction getDeposit() {
        return Transaction.builder()
                .bankAccount(getAccount())
                .type(TransactionType.DEPOSIT)
                .amount(AMOUNT_DEPOSIT)
                .build();
    }

    private static BankAccount getAccount() {
        return BankAccount.builder()
                .bankUser(getUser())
                .type(AccountType.SAVING)
                .build();
    }

    private static BankUser getUser() {
        return BankUser.builder()
                .username(USERNAME)
                .build();
    }
}
