package lt.danske.exercise.config;

import lt.danske.exercise.domain.BankUser;
import lt.danske.exercise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private UserRepository repository;

    @Autowired
    public DataLoader(UserRepository repository) {
        this.repository =  repository;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        BankUser user = BankUser.builder()
                .username("ruggero")
                .build();
        repository.save(user);
    }
}
