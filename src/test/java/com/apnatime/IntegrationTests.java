package com.apnatime;

import com.apnatime.domain.GenerateRandomDataRequest;
import com.apnatime.domain.GenerateRandomDataResponse;
import com.apnatime.service.IUserService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {IntegrationTests.Initializer.class})
@ActiveProfiles("test")
public class IntegrationTests {

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres")
            .withDatabaseName("apnatime")
            .withUsername("apnatime")
            .withPassword("apnatime");

    @Value("http://localhost:${local.server.port}")
    String baseUrl;

    @Autowired
    IUserService userService;

    @Autowired
    TestRestTemplate restTemplate;

    @Before
    public void initialize(){
    }

    @Test
    public void generateRandomDataTest() throws IOException {
        GenerateRandomDataRequest generateRandomDataRequest = new GenerateRandomDataRequest();
        generateRandomDataRequest.setNumberOfUsers(10);
        generateRandomDataRequest.setMaxNumberOfFriendships(4);
        ResponseEntity<?> postResponse = restTemplate.postForEntity(baseUrl + "/generateRandomData", generateRandomDataRequest, GenerateRandomDataResponse.class);
        GenerateRandomDataResponse response = (GenerateRandomDataResponse) postResponse.getBody();
        assertNotNull(response);
        assertEquals(response.getIsSuccessful(), true);
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "databaseServer=" + postgres.getJdbcUrl().substring(5),
                    "databaseUsername=" + postgres.getUsername(),
                    "databasePassword=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }


}
