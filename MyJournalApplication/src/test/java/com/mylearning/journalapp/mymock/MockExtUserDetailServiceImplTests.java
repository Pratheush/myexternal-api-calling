package com.mylearning.journalapp.mymock;

import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.repository.UserRepository;
import com.mylearning.journalapp.service.UserDetailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

/**
 * @MockitoAnnotations.initMocks(this) is a method from Mockito used to initialize mocks and
 * inject them into the test class. This method is required in earlier versions of Mockito
 * to explicitly initialize mock objects annotated with @Mock, @Spy, etc., and to inject them into the test class.
 *
 * @Mock creates a mock instance of UserRepository and @InjectMocks injects that mock into UserDetailServiceImpl
 *
 * here there is no Application Context since we have not used @SpringBootTest.
 * Since We have not used @SpringBootTest we can use @Mock instead of @MockBean for creating the Mock Instance of UserRepository
 * and we can @InjectMocks UserDetailServiceImpl instead of @Autowired
 *
 * In JUNIT 5:::
 * The @ExtendWith(MockitoExtension.class) annotation tells JUnit 5 to use Mockito to initialize the mock objects and handle the injection.
 * Instead of @MockitoAnnotations.initMocks(this) method from Mockito we can use @ExtendWith(MockitoExtension.class)
 * to initialize mocks object which are annotated with @Mock, @Spy and inject them into the test class which are annotated with @InjectMocks
 *
 * In JUNIT 4:::
 * Instead of @MockitoAnnotations.initMocks(this) method from Mockito we can use @RunWith(MockitoJUnitRunner.class)
 * to initialize mocks object which are annotated with @Mock, @Spy and inject them into the test class which are annotated with @InjectMocks
 * JUnit requires the test class to be public and to have exactly one public, no-argument constructor.
 *
 * In JUnit 4, another alternative is using the @RunWith(MockitoJUnitRunner.class) annotation.
 * This automatically initializes the mocks and injects them into the test class without needing to explicitly call initMocks.
 *
 * Using @RunWith(MockitoJUnitRunner.class) eliminates the need for MockitoAnnotations.initMocks(this).
 *
 * ----------------------------------------------------------------
 *
 * In JUnit 4, another alternative is using the MockitoJUnit.rule() approach, which allows Mockito's initialization using a JUnit @Rule.
 * @Rule
 * public MockitoRule mockitoRule = MockitoJUnit.rule();
 *
 * The MockitoRule takes care of initializing mocks and injecting dependencies.
 *
 * =============================================================================================
 *
 * @MockitoAnnotations.initMocks(this) is the older approach (mostly used in JUnit 4) to initialize mocks.
 * @ExtendWith(MockitoExtension.class) is the preferred and modern approach for JUnit 5 to initialize mocks.
 * @RunWith(MockitoJUnitRunner.class) is used in JUnit 4 as an alternative to initMocks.
 * MockitoJUnit.rule() provides a more flexible rule-based approach in JUnit 4.
 *
 * =========================================================================================================================
 *
 * when we use @SpringBootTest then this will also cause ApplicationContext get ready in background therefore we can
 * @Autowired UserService or any other spring component class that we want to test its methods and @MockBean if there is
 * any dependency injection to initialize Mock Object.
 *
 * But suppose in our project in our test class's test method which we are going to test if there are two dependencies suppose
 * one dependency is UserRepository which we want to create and Initialize Mock Object and Suppose another dependency is ReddisRepository
 * and we want to Autowired and Initialize the ReddisRepository then there we should use @SpringBootTest and @Autowired the test class and
 * @MockBean for dependency which we want to mock and @Autowired another dependency which we want to get from application-context
 *================================================================================================================
 *
 * @InjectMocks annotated UserDetailsServiceImpl here @InjectMocks will create instance of UserDetailsServiceImpl and look for
 * @Mock or @Spy annotated mock object here UserRepository is the dependency which will be injected into UserDetailsServiceImpl
 * after initializing the Mock Object using @ExtendWith(MockitoExtension.class)
 *
 * use @Spy annotation to spy on an existing instance
 * we can use Mockito Spy to partial mock an object. When we spy on an object, the real methods are being called unless its stubbed.
 *
 *
 *
 */

@ExtendWith(MockitoExtension.class)
class MockExtUserDetailServiceImplTests {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailServiceImpl userService;

    @Test
    @Disabled
    void testLoadUserByUsername(){
        // Arrange
        User user = User.builder().userName("raj").email("raj@gmail.com").password("rajencryptedpasswd").roles(List.of("USER")).build();

        // Mock the repository response
        Mockito.when(userRepository.findByUserName(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("raj");

        // Assert
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(user.getUserName(),userDetails.getUsername());  // assert the specific content

        // Verify findByUserName() was called exactly once
        Mockito.verify(userRepository,Mockito.times(1)).findByUserName(ArgumentMatchers.anyString());
    }
}
