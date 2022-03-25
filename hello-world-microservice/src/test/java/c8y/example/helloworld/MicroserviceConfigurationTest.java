package c8y.example.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MicroserviceConfigurationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void shouldLoadMicroserviceContext() {
        assertThat(applicationContext).isNotNull();
    }

}
