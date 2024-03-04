package lt.danske.exercise.repository;

import lt.danske.exercise.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM TRANSACTION WHERE ACCOUNT_ID = :accountId", nativeQuery = true)
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
}
