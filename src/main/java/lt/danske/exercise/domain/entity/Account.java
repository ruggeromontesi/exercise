package lt.danske.exercise.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lt.danske.exercise.domain.Currency;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToMany(mappedBy = "account")
    @JsonIgnore
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
    private AccountType type;
    @NotNull
    private Currency currency;
}
