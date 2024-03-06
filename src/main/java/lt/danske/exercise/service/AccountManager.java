package lt.danske.exercise.service;

import lombok.RequiredArgsConstructor;
import lt.danske.exercise.domain.entity.AccountType;
import lt.danske.exercise.domain.TransactionStatus;
import lt.danske.exercise.domain.TransactionType;
import lt.danske.exercise.domain.dto.BalanceDto;
import lt.danske.exercise.domain.dto.CreateAccountDto;
import lt.danske.exercise.domain.dto.RequestTransaction;
import lt.danske.exercise.domain.entity.Account;
import lt.danske.exercise.domain.entity.Customer;
import lt.danske.exercise.domain.entity.Transaction;
import lt.danske.exercise.exceptions.AccountNotFoundException;
import lt.danske.exercise.exceptions.InvalidInputException;
import lt.danske.exercise.exceptions.UserNotFoundException;
import lt.danske.exercise.repository.AccountRepository;
import lt.danske.exercise.repository.TransactionRepository;
import lt.danske.exercise.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountManager implements AccountManagementUseCase {
    public static final int COUNT_OF_MOST_RECENT_TRANSACTIONS = 10;
    public static final String MISSING_ACCOUNT_TYPE = "Missing account type";
    public static final String MISSING_CURRENCY = "Missing currency";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be positive";
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Account createAccount(CreateAccountDto accountDto) {
        validateCreateAccount(accountDto);
        Customer user = getBankUser(accountDto.getUserId());
        Account account = Account.builder()
                .customer(user)
                .type(accountDto.getAccountType())
                .currency(accountDto.getCurrency())
                .build();

        return accountRepository.saveAndFlush(account);
    }

    private Customer getBankUser(long userId) {
        return customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateCreateAccount(CreateAccountDto accountDto) {
        if (accountDto.getAccountType() == null) {
            throw new InvalidInputException(MISSING_ACCOUNT_TYPE);
        }

        if (accountDto.getCurrency() == null) {
            throw new InvalidInputException(MISSING_CURRENCY);
        }
    }

    public Transaction executeTransaction(RequestTransaction transactionDto) {
        validate(transactionDto);
        Account account = getAccount(transactionDto.getAccountId());
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(transactionDto.getType())
                .amount(transactionDto.getAmount())
                .status(getTransactionStatus(transactionDto, account.getType()))
                .build();

        return transactionRepository.save(transaction);
    }

    private static void validate(RequestTransaction transactionDto) {
        if (transactionDto.getAmount() <= 0) {
            throw new InvalidInputException(AMOUNT_MUST_BE_POSITIVE);
        }
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private TransactionStatus getTransactionStatus(RequestTransaction transactionDto, AccountType accountType) {
        if (isTransactionCausingNotAllowedOverdraft(transactionDto, accountType)) {
            return TransactionStatus.FAILURE_NOT_ENOUGH_BALANCE;
        }
        return TransactionStatus.SUCCESS;
    }

    private boolean isTransactionCausingNotAllowedOverdraft(RequestTransaction transactionDto, AccountType type) {
        return type == AccountType.SAVING
                && transactionDto.getType() == TransactionType.WITHDRAWAL
                && getBalanceAmount(transactionDto.getAccountId()) < transactionDto.getAmount();
    }

    public BalanceDto getBalance(long accountId) {
        Account account = getAccount(accountId);
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
                .sorted(Comparator.comparingLong((Transaction t) -> t.getCreated().toEpochSecond(ZoneOffset.UTC))
                        .thenComparingLong(Transaction::getId).reversed()
                )
                .limit(COUNT_OF_MOST_RECENT_TRANSACTIONS)
                .toList();
    }
}
