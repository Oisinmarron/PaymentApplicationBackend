package com.ezycollect.paymentapplication.persistence.dao;

import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.persistence.entity.PaymentDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest // Use @SpringBootTest to load the full application context
@ActiveProfiles("integration-test") // Activate the integration-test profile
@Testcontainers // Enable Testcontainers support
public class PaymentDaoIT {

    @Autowired
    private PaymentDao paymentDao;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test")
            .withUsername("root")
            .withPassword("Password_1");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        // Dynamically set the DataSource properties to use the MySQL container
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Save payment details")
    public void test_savePaymentDetails() {

        PaymentDetails paymentDetails = new PaymentDetails("Oisin", "Marron", "NSW 2033", "hPWEu+ufgH8AtPQ8pYsK/DF2uVC+HLOU+Nhg9pEUgfg=");

        assertThat(paymentDetails.getPaymentId(), is(0));

        paymentDao.savePaymentDetails(paymentDetails);

        assertThat(paymentDetails.getPaymentId(), is(2));
        assertThat(paymentDetails.getFirstName(), is("Oisin"));
        assertThat(paymentDetails.getLastName(), is("Marron"));
        assertThat(paymentDetails.getZipCode(), is("NSW 2033"));
        assertThat(paymentDetails.getCardNumber(), is("hPWEu+ufgH8AtPQ8pYsK/DF2uVC+HLOU+Nhg9pEUgfg="));
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get payment details by paymentId, as a dto object")
    public void test_getPaymentDetails() {

        PaymentDetailsDto paymentDetailsDto = paymentDao.getPaymentDetailsById(1);

        assertThat(paymentDetailsDto.getFirstName(), is("Oisin"));
        assertThat(paymentDetailsDto.getLastName(), is("Marron"));
        assertThat(paymentDetailsDto.getZipCode(), is("NSW 2033"));
        assertThat(paymentDetailsDto.getCardNumber(), is("hPWEu+ufgH8AtPQ8pYsK/DF2uVC+HLOU+Nhg9pEUgfg="));
    }
}
