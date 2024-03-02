package lt.danske.exercise.helper;

import lt.danske.exercise.domain.AccountType;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.BankUser;
import lt.danske.exercise.domain.entity.Transaction;

import java.util.List;

public class TestHelper {
    public static long USER_ID = 1001L;
    public static final String USERNAME = "TEST_USERNAME";

    public static List<Transaction> getTransactions() {
        BankUser user = BankUser.builder()
                .username("ruggero")
                .build();

        BankAccount account = BankAccount.builder()
                .bankUser(user)
                .type(AccountType.SAVING)
                .build();


        Transaction t1 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .build();

        Transaction t2 = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .build();

        return  List.of(t1,t2);

    }
}
