package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_advance;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SpyTest {

    /**
     * We’ll talk about the @Spy annotation and how to stub a spy.
     *
     * Mockito.spy() or use @Spy annotation to spy on a real object.This will allow us to call all the normal
     * methods of the object while still tracking every interaction, just as we would with a mock.
     *
     * To enable Mockito annotations (such as @Spy, @Mock, … ),
     * we need to use @ExtendWith(MockitoExtension.class) that initializes mocks and handles strict stubbings.
     *
     *  to stub a Spy We can configure/override the behavior of a method using the same syntax we would use with a mock.
     *
     * A spy will actually call the real implementation of the add method and add the element to the underlying list:
     *
     * The mock simply creates a bare-bones shell instance of the Class
     * the spy will wrap an existing instance behave in the same way as the normal instance
     *
     * The Mockito when() method expects a mock or spy object as the argument.
     *
     * // we can use an ArgumentCaptor with stubbing, we should generally avoid doing so this means avoiding using
     * // an ArgumentCaptor with Mockito.when. With stubbing, we should use an ArgumentMatcher instead.
     *
     *  after creating mock object or spy object we can then use the mock to stub return values for its methods and verify
     *  if they were called.
     *
     * @ExtendsWith annotation accepts any class that implements the Extension interface
     * in Junit5 do not use @RunWith(SpringRunner.class instead use @ExtendWith(SpringExtension.class,MockitoExtension.class)
     *
     * if we are using Spring Boot and need to mock a bean from the Spring Application Context then use @MockBean instead of simple @Mock
     *
     * @SpringBootTest :: provides full Spring ApplicationContext for test means all the beans and configurations in our application are loaded just like when the actual application runs.
     * can be used for Unit test and Integration Tests. for unit test we can use @MockBean or @SpyBean for mocking and spying on our beans.
     * .@SpringBootTest provides TestRestTemplate and WebTestClient beans to call REST APIs in our tests
     * @SpringBootTest allows to set custom environment properties for tests.
     *
     * @MockBean and @SpyBean annotation is useful in Integration tests where a particular bean like an external service needs to be mocked.
     * @MockBean and @SpyBean add mock objects and spy objects to the Spring Application Context.
     * The mock will replace any existing bean of the same type in the Spring Application Context and if no bean is found of same type then new will be added.
     *
     *
     * @ContextConfiguration(classes={SpringTestConfiguration.class})
     * @ContextConfiguration annotation in spring is used to specify the classes that will be used to load the application context for Integration Tests
     * this tells Spring to load the application context using SpringTestConfiguration class
     *
     *
     * @ExtendWith(SpringExtension.class,MockitoExtension.class)
     * //@RunWith(SpringRunner.class)
     * public class MockBeanAnnotationIntegrationTest {
     *
     *     @MockBean
     *     UserRepository mockRepository;
     *
     *     @Autowired
     *     ApplicationContext context;
     *
     *     @Test
     *     public void givenCountMethodMocked_WhenCountInvoked_ThenMockValueReturned() {
     *         Mockito.when(mockRepository.count()).thenReturn(123L);
     *
     *         UserRepository userRepoFromContext = context.getBean(UserRepository.class);
     *         long userCount = userRepoFromContext.count();
     *
     *         Assert.assertEquals(123L, userCount);
     *         Mockito.verify(mockRepository).count();
     *     }
     * }
     *
     *
     */

    @Spy
    List<String> spiedList = new ArrayList<String>();

    @AfterEach
    public void teardown() {
        spiedList.clear();
    }


    // Simple Spy Example
    // the real method add() is actually called and the size of spyList becomes 2.
    @Test
    void givenUsingSpyMethod_whenSpyingOnList_thenCorrect() {
        List<String> list = new ArrayList<String>();
        List<String> spyList = Mockito.spy(list);

        spyList.add("one");
        spyList.add("two");

        Mockito.verify(spyList).add("one");
        Mockito.verify(spyList).add("two");

        Assertions.assertThat(spyList).hasSize(2);
    }

    // The @Spy Annotation
    @Test
    void givenUsingSpyAnnotation_whenSpyingOnList_thenCorrect() {
        spiedList.add("one");
        spiedList.add("two");

        Mockito.verify(spiedList).add("one");
        Mockito.verify(spiedList).add("two");

        Assertions.assertThat(spiedList).hasSize(2);
    }

    // 4. Stubbing a Spy here we are using doReturn()
    @Test
    void givenASpy_whenStubbingTheBehaviour_thenCorrect() {
        List<String> list = new ArrayList<String>();
        List<String> spyList = Mockito.spy(list);

        org.junit.jupiter.api.Assertions.assertEquals(0, spyList.size());

        Mockito.doReturn(100).when(spyList).size();
        Assertions.assertThat(spyList).hasSize(100);

    }

   /* @Test
    void testJustSoYouKnow(){

        final List<String> spyList = Mockito.spy(new ArrayList<>());
        Assertions.assertThatNoException().isThrownBy(() -> Mockito.doReturn(100).when(spyList).size());
    }*/

}
