package lt.danske.exercise.repository;

import lt.danske.exercise.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT * FROM ACCOUNT WHERE CUSTOMER_ID = :customerId", nativeQuery = true)
    List<Account> findByCustomerId(@Param("customerId") Long customerId);
}
