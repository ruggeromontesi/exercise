package lt.danske.exercise.repository;

import lt.danske.exercise.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<BankAccount, Long> {

    @Query(value = "SELECT * FROM BANK_ACCOUNT WHERE BANKUSER_ID = :userId", nativeQuery = true)
    List<BankAccount> findByUserId(@Param("userId")Long userId);
}
