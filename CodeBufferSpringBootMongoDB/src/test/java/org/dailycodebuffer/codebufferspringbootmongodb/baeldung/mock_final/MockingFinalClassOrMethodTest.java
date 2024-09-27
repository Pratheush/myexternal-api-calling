package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mock_final;

import org.assertj.core.api.Assertions;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockingFinalClassOrMethodTest {

    // Mock a Final Method
    // By creating a concrete instance and a mock instance of MyList,
    // we can compare the values returned by both versions of finalMethod() and verify that the mock is called.
    @Test
    void whenMockFinalMethod_thenMockWorks() {

        MyList myList = new MyList();

        MyList mock = Mockito.mock(MyList.class);
        Mockito.when(mock.finalMethod()).thenReturn(1);

        Integer resultMyList=myList.finalMethod();
        Integer resultMock=mock.finalMethod();
        // comparing both values of mock instance and MyList instance finalMethod() return values::
        MatcherAssert.assertThat(resultMock, CoreMatchers.not(CoreMatchers.is(resultMyList)));

        org.junit.jupiter.api.Assertions.assertEquals(1,resultMock);
        Assertions.assertThat(mock.finalMethod()).isNotZero();
    }

    // Mock a Final Class
    // Mocking a final class is just as easy as mocking any other class:
    // create a concrete instance and a mock instance of our final class,
    // mock a method and verify that the mocked instance behaves differently.
    @Test
    public void whenMockFinalClass_thenMockWorks() {

        FinalList mock = Mockito.mock(FinalList.class);
        Mockito.when(mock.size()).thenReturn(2);

        Assertions.assertThat(mock.size()).isNotEqualTo(1);
    }


}
