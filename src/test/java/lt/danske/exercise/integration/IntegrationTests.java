package lt.danske.exercise.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTests {


    void shouldCreateAccount() {
        RestTemplate restTemplate = new RestTemplate();
    }
}
