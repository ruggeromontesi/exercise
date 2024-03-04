package lt.danske.exercise.service;

import lombok.RequiredArgsConstructor;
import lt.danske.exercise.domain.dto.BalanceDto;
import lt.danske.exercise.domain.dto.CreateAccountDto;
import lt.danske.exercise.domain.dto.TransactionDto;
import lt.danske.exercise.domain.TransactionStatus;
import lt.danske.exercise.domain.TransactionType;
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

import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountManager implements AccountManagementUseCase {
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

        return accountRepository.saveAndFlush(account);
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

    public Transaction executeTransaction(TransactionDto transactionDto) {
        BankAccount account = getAccount(transactionDto.getAccountId());

        Transaction transaction = Transaction.builder()
                .bankAccount(account)
                .type(transactionDto.getType())
                .amount(transactionDto.getAmount())
                .status(getTransactionStatus(transactionDto))
                .build();
        return transactionRepository.save(transaction);
    }

    private BankAccount getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private TransactionStatus getTransactionStatus(TransactionDto transactionDto) {

        if(transactionDto.getType() == TransactionType.WITHDRAW && getBalanceAmount(transactionDto.getAccountId()) < transactionDto.getAmount()) {
            return TransactionStatus.FAILURE_NOT_ENOUGH_BALANCE;
        }
        return TransactionStatus.SUCCESS;
    }

    public BalanceDto getBalance(long accountId) {
        BankAccount account = getAccount(accountId);
        double amount = getBalanceAmount(accountId);

        return BalanceDto.builder()
                .amount(amount)
                .currency(account.getCurrency())
                .build();
    }

    private double getBalanceAmount(long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .mapToDouble(t -> t.getAmount() * t.getType().getMultiplier())
                .sum();
    }

    public List<Transaction> getRecentTransactions(long accountId) {
        getAccount(accountId);
        return transactionRepository.findByAccountId(accountId).stream()
                .sorted(Comparator.comparingLong((Transaction t) -> t.getCreated().toEpochSecond(ZoneOffset.UTC)).reversed()
                        .thenComparingLong(Transaction::getId).reversed())
                .limit(COUNT_OF_MOST_RECENT_TRANSACTIONS)
                .toList();
    }
}
