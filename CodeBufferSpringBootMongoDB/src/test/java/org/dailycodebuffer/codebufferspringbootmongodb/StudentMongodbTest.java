package org.dailycodebuffer.codebufferspringbootmongodb;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.repository.StudentRepository;
import org.dailycodebuffer.codebufferspringbootmongodb.service.StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.dailycodebuffer.codebufferspringbootmongodb.MongoDBTestContainerConfig.mongoDBContainer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
//@DataMongoTest
@AutoConfigureDataMongo
@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class}) // used in JUnit 5 to tell test framework to extend behavior with Mockito enables the use of annotations like @Mock,@InjectMocks etc in test classes
@ComponentScan(basePackages = "org.dailycodebuffer.codebufferspringbootmongodb")
@Profile("student")
public class StudentMongodbTest {

    /**
     * @DataMongoTest annotation in Spring Boot is used to configure and set up a slice of the
     * Spring context specifically tailored for testing MongoDB components in a Spring Data MongoDB application.
     * It is similar to @DataJpaTest but is designed for MongoDB.
     *
     * The @DataJpaTest annotation in Spring Boot is used to configure and set up Spring context  for testing JPA
     * components in a Spring Data JPA application
     *
     * a test class or test method annotated with @DataJpaTest triggers the following behaviors:
     * 1. Configure a Test ApplicationContext:
     *    It sets up a minimal Spring context only containing the beans necessary for JPA testing.
     *    This typically includes the JPA entities, repositories, and other related components.
     * 2. Use an Embedded Database:
     *    By default, @DataJpaTest uses an embedded in-memory database to provide a lightweight and fast database for testing
     *    This helps to isolate the tests from the actual database and improves test performance.
     *    Uses an Embedded MongoDB in @DataMongoTest when this annotation is used.
     *
     *  3. Transaction Management:
     *     It automatically configures and manages transactions for your test methods.
     *     Each test method is typically executed within its own transaction, and the changes made
     *     during the test are rolled back after the test method completes. This ensures that
     *     the database is in a consistent state before and after each test.
     *
     *  4. Disable Full Auto-Configuration:
     *     It disables full auto-configuration and only configures the necessary components for JPA testing.
     *
     * you can use @DataJpaTest in conjunction with @SpringBootTest
     * But If you need to test a broader set of components, including those outside of JPA, then choose @SpringBootTest
     * However If you specifically want to focus on testing JPA-related components like entities and repositories then choose @DataJpaTest
     *
     *
     *
     * @DataJpaTest
     * @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
     * public class UserRepositoryTest { @Autowired private UserRepository userRepository;}
     *
     *
     *@AutoConfigureTestDatabase annotation in Spring Boot is used to customize the configuration of the
     * test database when running integration tests. The replace attribute allows you to control whether
     * the auto-configured test database should replace the main application database configuration or not.
     *
     * The replace = AutoConfigureTestDatabase.Replace.NONE specifies that
     * the test database configuration should not replace the main application database configuration.
     *
     */

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;


    @BeforeAll
    static void setUp(){
        var mappedPort = mongoDBContainer.getMappedPort(27017);
        System.setProperty("mongodb.container.port", String.valueOf(mappedPort));
        mongoDBContainer.start();

    }

    @AfterAll
    static void tearDown(){
        mongoDBContainer.stop();
    }

    // JSON QUERY METHOD TESTING ----------------------------------------------------------------
    @Test
    void testGetStudentByFirstNameAndLastName(){

        Address address= Address.builder()
                .address1("Kerala")
                .address2("Kochi")
                .city("Indirapuram")
                .build();
        String dateString = "2024-01-06T19:17:33.091";
        LocalDateTime dateTime = LocalDateTime.parse(dateString);
        Student student= Student.builder()
                .firstName("Ravi")
                .lastName("Kumar")
                .totalSpendInBooks(BigDecimal.valueOf(3700))
                 .email("erickent@gmail.com")
                 .gender(Gender.MALE)
                 .age(27)
                 .favSubjects(Arrays.asList("Coding", "Watching Kdramas", "Playing Chess"))
                 .address(address)
                 .createdAt(dateTime)
                 .updatedAt(null)
                .build();
        Mockito.when(studentRepository.getStudentsByFirstNameAndLastName("Ravi","Kumar")).thenReturn(List.of(student));

        List<Student> studentList=studentService
                .getStudentByFirstNameAndLastName(student.getFirstName(), student.getLastName());

        assertEquals(1,studentList.size());
        assertEquals("Ravi",studentList.get(0).getFirstName());
        assertThat(studentList.getFirst().getLastName(),is("Kumar"));

    }



}
