package lt.danske.exercise.service;

import lombok.RequiredArgsConstructor;
import lt.danske.exercise.controller.BalanceDto;
import lt.danske.exercise.controller.CreateAccountDto;
import lt.danske.exercise.controller.TransactionDto;
import lt.danske.exercise.domain.entity.BankAccount;
import lt.danske.exercise.domain.entity.BankUser;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.exceptions.AccountNotFoundException;
import lt.danske.exercise.exceptions.InvalidInputException;
import lt.danske.exercise.exceptions.UserNotFoundException;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountManager implements AccountManagementUseCase{
    public static final int COUNT_OF_MOST_RECENT_TRANSACTIONS = 10;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankAccount createAccount(CreateAccountDto accountDto) {
        validateCreateAccount(accountDto);
        BankUser user = getBankUser(accountDto.getUserId());
        BankAccount account = BankAccount.builder()
                .bankUser(user)
                .type(accountDto.getAccountType())
                .currency(accountDto.getCurrency())
                .build();

        return accountRepository.save(account);
    }

    private BankUser getBankUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateCreateAccount(CreateAccountDto accountDto) {
        if (accountDto.getAccountType() == null) {
            throw new InvalidInputException("Missing account type");
        }

        if (accountDto.getCurrency() == null) {
            throw new InvalidInputException("Missing currency");
        }
    }

    public void executeTransaction(TransactionDto transactionDto) {
        BankAccount account = getAccount(transactionDto.getAccountId());

        Transaction transaction = Transaction.builder()
                .bankAccount(account)
                .type(transactionDto.getType())
                .amount(transactionDto.getAmount())
                .build();
        transactionRepository.save(transaction);
    }

    private BankAccount getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public BalanceDto getBalance(long accountId) {
        BankAccount account = getAccount(accountId);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        double amount = transactions.stream()
                .mapToDouble(t -> t.getAmount() * t.getType().getMultiplier())
                .sum();

        return BalanceDto.builder()
                .amount(amount)
                .currency(account.getCurrency())
                .build();
    }

    public List<Transaction> getRecentTransactions(long accountId) {
        getAccount(accountId);
        return transactionRepository.findByAccountId(accountId).stream()
                .sorted(Comparator.comparingLong(Transaction::getId).reversed())
                .limit(COUNT_OF_MOST_RECENT_TRANSACTIONS)
                .toList();
    }
}
