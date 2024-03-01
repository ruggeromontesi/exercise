package lt.danske.exercise.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class BankUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String password;

    @OneToMany(mappedBy = "bankUser")
    private List<BankAccount>  accounts;

    //...
}
