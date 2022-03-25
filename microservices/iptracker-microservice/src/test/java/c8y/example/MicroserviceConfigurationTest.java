package c8y.example;

import com.cumulocity.microservice.settings.service.MicroserviceSettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MicroserviceConfigurationTest {

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    MicroserviceSettingsService microserviceSettingsService;

    @Test
    void shouldLoadMicroserviceContext() {
        assertThat(applicationContext).isNotNull();
    }

}
