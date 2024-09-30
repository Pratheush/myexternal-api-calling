package com.mylearning.journalapp.service;

import com.mongodb.client.MongoClients;
import com.mylearning.journalapp.config.TestContainerConfig;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.exception.UserNotFoundException;
import com.mylearning.journalapp.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Locale;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
//@Import(value = {TestContainerConfig.class})
@Slf4j
class UserServiceTests {

    //@ServiceConnection
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.7"));
            //.withEnv("MONGO_REPLICA_SET", "rs0");
            //.withCommand("--replSet rs0 --sslMode disabled")
            //.withExposedPorts(27017)
            //.waitingFor(Wait.forListeningPort());

    // : URI ::: mongodb://localhost:64930/test
    // mongodb://localhost:27017/test?connectTimeoutMS=10000&socketTimeoutMS=30000&ssl=false
    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        //registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.uri",() -> mongoDBContainer.getReplicaSetUrl() + "?connectTimeoutMS=10000&socketTimeoutMS=30000&ssl=false");
        registry.add("spring.data.mongodb.database",() -> "test");
        log.info("URI ::: {}", mongoDBContainer.getReplicaSetUrl());


    }
    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
        //executeInitReplicaSetCommand();
    }


    @Autowired
    private Environment environment;  // To access properties directly in your method

    private static void executeInitReplicaSetCommand() {
        var client = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        Document result = client.getDatabase("admin").runCommand(new Document("replSetGetStatus", 1));
        if (result.containsKey("ok") && result.getDouble("ok") == 1.0) {
            System.out.println("Replica set already initialized.");
        } else {
            client.getDatabase("admin").runCommand(new Document("replSetInitiate", new Document()));
        }
    }
    @AfterAll
    static void tearDown(){
        mongoDBContainer.stop();
    }

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        //RestAssured.defaultParser = Parser.TEXT;
        //String mongoUri = environment.getProperty("spring.data.mongodb.uri");
        //System.out.println("MongoDB URI: " + mongoUri);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void initDBSetupSavedUser() {
        User abc = new User(null, "ABC", "ABC@gmail.com",true, "ABC123", null, List.of("USER"));
        User def = new User(null, "DEF", "DEF@gmail.com", false,"DEF123", null, List.of("USER"));
        User ghi = new User(null, "GHI", "GHI@gmail.com",true, "GHI123", null, List.of("USER"));
        List<User> userList = getUsers(abc, def, ghi);
        userRepository.saveAll(userList);  // Saves the users into the test database
    }

    @NotNull
    private static List<User> getUsers(User abc, User def, User ghi) {
        User raju = new User(null, "raju", "raju@gmail.com",true, "raju123", null, List.of("USER"));
        User raj = new User(null, "raj", "raj@gmail.com",false, "raj123", null, List.of("USER"));
        User vibha = new User(null, "vibha", "vibha@gmail.com",true, "vibha123", null, List.of("USER"));
        User shyam = new User(null, "shyam", "shyam@gmail.com",true, "shyam123", null, List.of("USER"));
        User gaurav = new User(null, "gaurav", "gaurav@gmail.com",false, "gaurav123", null, List.of("USER"));
        return List.of(abc, def, ghi,raju,raj,vibha,shyam,gaurav);
    }

    @AfterEach
    void tearDownDBSetupSavedUser(){
        userRepository.deleteAll();
    }

    //@Disabled
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

    /**
     * Assertions.assertNotNull(userRepository.findByUserName(userName));
     * Null Check Logic: The current logic will always assert non-null for Optional<User>,
     * but to actually test for the user’s presence, you should assert userRepository.findByUserName(userName).isPresent().
     * This way, the test checks whether the user exists in the repository.
     *
     *
     * @param userName
     */
    @ParameterizedTest
    @CsvFileSource(resources = {"username.csv"},numLinesToSkip = 1, lineSeparator = "\n", delimiter = ':')
    void testFindByUserName(String userName){
        Assertions.assertTrue(
                userRepository.findByUserName(userName).isPresent(),
                String.format("Test Failed For %s: User not found", userName)
        );
    }


    @ParameterizedTest
    @ArgumentsSource(value = UserArgumentProvider.class)
    void testAddUser(User user) {
       // User user1 = new User(null, "ABC", "ABC@gmail.com", "ABC123", null, List.of("USER"));

        var responseBodyMsg = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/public")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        MatcherAssert.assertThat(String.format("Test Failed For the User %s :",user.getUserName()),responseBodyMsg, Matchers.equalTo(String.format("User created : %s",user.getUserName())));

    }

    @ParameterizedTest
    @ValueSource(strings = { "ABC", "DEF", "GHI" })
    void testGetUserByUsername(String username) {
        User userByUsername = userService.getUserByUsername(username);
        Assertions.assertNotNull(userByUsername, String.format("Test Failed for username : %s ", username));
        Assertions.assertEquals(username, userByUsername.getUserName(),String.format("TEST FAILED FOR USER :: %s",username));
    }

    @ParameterizedTest
    @ValueSource(strings = { "RAWAT", "BAWAL", "JHAKKAS" })
    void testGetUserByUsername_UserNotFound(String username) {
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByUsername(username);
        }, String.format("Expected exception for user: %s", username));
    }
}
