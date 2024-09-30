package com.mylearning.journalapp.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mylearning.journalapp.config.TestContainerConfig;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.exception.UserNotFoundException;
import com.mylearning.journalapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@Slf4j
@Import(value = {TestContainerConfig.class})
class MyUserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    /*@BeforeAll
    public static void setUp() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase adminDatabase = mongoClient.getDatabase("admin");
            adminDatabase.runCommand(new Document("replSetInitiate", new Document()));
        }
    }*/

    @BeforeEach
    void initDBSetupSavedUser() {
        User abc = new User(null, "ABC", "ABC@gmail.com",true, "ABC123", null, List.of("USER"));
        User def = new User(null, "DEF", "DEF@gmail.com", false,"DEF123", null, List.of("USER"));
        User ghi = new User(null, "GHI", "GHI@gmail.com",true, "GHI123", null, List.of("USER"));
        List<User> userList = List.of(abc, def, ghi);
        userRepository.saveAll(userList);  // Saves the users into the test database
    }

    @AfterEach
    void tearDownDBSetupSavedUser(){
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1,1,2",
            "3,2,5",
            "4,3,7",
            "2,7,9"
    })
    void testAdd(int x, int y, int expected){
        Assertions.assertEquals(expected,x+y,String.format("Test Failed For %d +%d = %d",x,y,expected));
    }

    @ParameterizedTest
    @ValueSource(strings = { "ABC", "DEF", "GHI" })
    void testGetUserByUsername(String username) {
        User userByUsername = userService.getUserByUsername(username);
        Assertions.assertNotNull(userByUsername, String.format("User %s should not be null", username));
        Assertions.assertEquals(username, userByUsername.getUserName(),String.format("TEST FAILED FOR USER :: %s",username));
    }

    @ParameterizedTest
    @ValueSource(strings = { "RAWAT", "BAWAL", "JHAKKAS" })
    void testGetUserByUsername_UserNotFound(String userName) {
        List<User> userList = userRepository.findAll();
        log.info("User list: {}" , userList);
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByUsername(userName);
        }, String.format("Expected exception for user: %s", userName));
    }

}
