package lt.danske.exercise.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bankuser_id")
    private BankUser bankUser;

    @OneToMany(mappedBy = "bankAccount")
    private List<Transaction> transactions;
}
