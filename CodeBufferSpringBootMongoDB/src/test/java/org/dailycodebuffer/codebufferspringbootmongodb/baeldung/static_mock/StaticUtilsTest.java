package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.static_mock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;

public class StaticUtilsTest{

    /**
     *
     * mock static methods with the help of  PowerMockito
     * we can use the Mockito.mockStatic(Class<T> classToMock) method to mock invocations to static method calls.
     * This method returns a MockedStatic object for our type, which is a scoped mock object.
     *
     * It’s important to note that scoped mocks must be closed by the entity that activates the mock.
     * This is why we define our mock within a try-with-resources construct so that the mock is closed automatically
     * when we finish with our scoped block. it assures that our static mock remains temporary otherwise during our
     * test runs calling static methods lead to adverse effects on our test results
     * due to the concurrent and sequential nature of running tests.
     *
     * tests run quite fast Mockito doesn’t need to replace the classloader for every test.
     *
     * In our example, we reiterate this point by checking, before and after our scoped block,
     * that our static method name returns a real value.
     *
     */

//     Mocking a No Argument Static Method
    @Test
    void givenStaticMethodWithNoArgs_whenMocked_thenReturnsMockSuccessfully() {
        Assertions.assertThat(StaticUtils.name()).isEqualTo("Baeldung");

        try (MockedStatic<StaticUtils> utilities = Mockito.mockStatic(StaticUtils.class)) {
            utilities.when(StaticUtils::name).thenReturn("Eugen");
            Assertions.assertThat(StaticUtils.name()).isEqualTo("Eugen");
        }

        Assertions.assertThat(StaticUtils.name()).isEqualTo("Baeldung");
    }

    // Mocking a Static Method With Arguments
    /**
     * we follow the same approach, but this time we use a lambda expression inside our
     * when clause where we specify the method along with any arguments that we want to mock.
     */
    @Test
    void givenStaticMethodWithArgs_whenMocked_thenReturnsMockSuccessfully() {
        Assertions.assertThat(StaticUtils.range(2, 6)).containsExactly(2, 3, 4, 5);

        try (MockedStatic<StaticUtils> utilities = Mockito.mockStatic(StaticUtils.class)) {
            utilities.when(() -> StaticUtils.range(2, 6))
                    .thenReturn(Arrays.asList(10, 11, 12));

            Assertions.assertThat(StaticUtils.range(2, 6)).containsExactly(10, 11, 12);
        }

        Assertions.assertThat(StaticUtils.range(2, 6)).containsExactly(2, 3, 4, 5);
    }
}
