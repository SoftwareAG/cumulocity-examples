package com.cumulocity.mibparser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "application.name=test",
        "logging.config="
})
class MicroserviceConfigurationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void shouldLoadMicroserviceContext() {
        assertThat(applicationContext).isNotNull();
    }

}
