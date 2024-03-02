package lt.danske.exercise.repository;

import lt.danske.exercise.domain.entity.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<BankUser, Long> {
}
