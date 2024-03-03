package lt.danske.exercise.helper;

import lt.danske.exercise.controller.Currency;
import lt.danske.exercise.controller.TransactionStatus;
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
    private static long i = 0;

    public static List<Transaction> getAllSuccessfulTransactions(int numberOfTransactions) {
        List<Transaction> deposits = IntStream.range(0, numberOfTransactions - 1).mapToObj(i -> getDeposit(AMOUNT_DEPOSIT)).toList();
        List<Transaction> withdrawal = List.of(getWithdrawal(AMOUNT_WITHDRAWAL, TransactionStatus.SUCCESS));
        return Stream.of(deposits, withdrawal).flatMap(Collection::stream).toList();
    }

    public static Transaction getWithdrawal(double amountWithdrawal, TransactionStatus status) {
        return Transaction.builder()
                .id(i++)
                .bankAccount(getAccount())
                .type(TransactionType.WITHDRAW)
                .amount(amountWithdrawal)
                .status(status)
                .build();
    }

    public static Transaction getDeposit(double amountDeposit) {
        return Transaction.builder()
                .id(i++)
                .bankAccount(getAccount())
                .type(TransactionType.DEPOSIT)
                .amount(amountDeposit)
                .status(TransactionStatus.SUCCESS)
                .build();
    }

    public static BankAccount getAccount() {
        return BankAccount.builder()
                .bankUser(getUser())
                .type(AccountType.SAVING)
                .currency(Currency.EUR)
                .build();
    }

    public static BankUser getUser() {
        return BankUser.builder()
                .id(USER_ID)
                .username(USERNAME)
                .accounts(List.of())
                .build();
    }

    public static List<Transaction> getSuccessfulAndUnsuccessfulTransactions() {
        return List.of(getDeposit(AMOUNT_DEPOSIT), getWithdrawal(10 * AMOUNT_WITHDRAWAL, TransactionStatus.FAILURE_NOT_ENOUGH_BALANCE));
    }
}
