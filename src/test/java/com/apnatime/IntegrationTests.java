package com.apnatime;

import com.apnatime.domain.GenerateRandomDataRequest;
import com.apnatime.domain.GenerateRandomDataResponse;
import com.apnatime.domain.SearchResult;
import com.apnatime.domain.UploadDataResponse;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void initialize() throws Exception {
        userService.clearDatabase();
    }

    @Test
    public void generateRandomDataTest() throws Exception {
        GenerateRandomDataRequest generateRandomDataRequest = new GenerateRandomDataRequest();
        generateRandomDataRequest.setNumberOfUsers(10);
        generateRandomDataRequest.setMaxNumberOfFriendships(4);
        ResponseEntity<?> postResponse = restTemplate.postForEntity(baseUrl + "/generateRandomData", generateRandomDataRequest, GenerateRandomDataResponse.class);
        GenerateRandomDataResponse response = (GenerateRandomDataResponse) postResponse.getBody();
        assertNotNull(response);
        assertEquals(response.getIsSuccessful(), true);
        userService.clearDatabase();
    }

    @Test
    public void uploadDataTest() throws Exception {
        userService.clearDatabase();

        File usersFile = new File(ClassLoader.getSystemClassLoader().getResource("users.csv").getFile());
        LinkedMultiValueMap<String,Object> requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("name","users.csv");
        requestEntity.add("file",new FileSystemResource(usersFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String,Object>> httpEntity = new HttpEntity(requestEntity, headers);

        ResponseEntity<UploadDataResponse> responseEntity =
                restTemplate.postForEntity(baseUrl + "/uploadUsersInformation", httpEntity, UploadDataResponse.class);

        UploadDataResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(response.getIsSuccessful(), true);
        assertEquals(response.getNumberOfRecordsAdded().longValue(), 12);

        File relationsFile = new File(ClassLoader.getSystemClassLoader().getResource("relations.csv").getFile());
        requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("name","users.csv");
        requestEntity.add("file",new FileSystemResource(relationsFile));

        headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        httpEntity = new HttpEntity(requestEntity, headers);

        responseEntity =
                restTemplate.postForEntity(baseUrl + "/uploadConnectionsInformation", httpEntity, UploadDataResponse.class);

        response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(response.getIsSuccessful(), true);
        assertEquals(response.getNumberOfRecordsAdded().longValue(), 18);





    }

    @Test
    public void findUsersAtNLevelTest() throws Exception {
        userService.clearDatabase();

        File usersFile = new File(ClassLoader.getSystemClassLoader().getResource("users.csv").getFile());
        LinkedMultiValueMap<String,Object> requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("name","users.csv");
        requestEntity.add("file",new FileSystemResource(usersFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String,Object>> httpEntity = new HttpEntity(requestEntity, headers);

        ResponseEntity<UploadDataResponse> responseEntity =
                restTemplate.postForEntity(baseUrl + "/uploadUsersInformation", httpEntity, UploadDataResponse.class);

        File relationsFile = new File(ClassLoader.getSystemClassLoader().getResource("relations.csv").getFile());
        requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("name","users.csv");
        requestEntity.add("file",new FileSystemResource(relationsFile));

        headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        httpEntity = new HttpEntity(requestEntity, headers);

        responseEntity =
                restTemplate.postForEntity(baseUrl + "/uploadConnectionsInformation", httpEntity, UploadDataResponse.class);

        headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search")
                .queryParam("userId", 1)
                .queryParam("level", 3);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<Object> response = restTemplate.exchange( builder.toUriString(), HttpMethod.GET, entity, Object.class);
        Object res =  response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<SearchResult> results = objectMapper.convertValue(res, new TypeReference<List<SearchResult>>(){});
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).getUser().getName(), "D");
        assertEquals(results.get(0).getLevel().intValue(), 3);
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
