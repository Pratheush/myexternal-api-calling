package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_basics;

import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.TooFewActualInvocations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;




@ExtendWith(MockitoExtension.class)
class MyListMockitoBasicTest {


    //==================================================================================================================

    // Simple Mocking
    @Test
    void testSimpleMocking(){
        MyList mockedMyList= Mockito.mock(MyList.class);
        Mockito.when(mockedMyList.add(Mockito.anyString())).thenReturn(false);

        Boolean added= mockedMyList.add(randomAlphabetic(6));

        MatcherAssert.assertThat(added, CoreMatchers.is(false));

        Mockito.verify(mockedMyList).add(Mockito.anyString());
    }

    // Mocking With Mock’s Name
    @Test
    void testMockWithMocksName(){
        MyList mockedMyList= Mockito.mock(MyList.class,"MyMOCKDLIST");
        Mockito.when(mockedMyList.add(Mockito.anyString())).thenReturn(false);

        Boolean added= mockedMyList.add(randomAlphabetic(6));

        MatcherAssert.assertThat(added, CoreMatchers.is(false));

        // wanted 2 times but was 1 times so exceptions thrown :: org.mockito.exceptions.verification.TooFewActualInvocations:
        // Mockito.verify(mockedMyList, Mockito.times(2)).add(Mockito.anyString());

        assertThatThrownBy(() -> Mockito.verify(mockedMyList, Mockito.times(2)).add(Mockito.anyString()))
                .isInstanceOf(TooFewActualInvocations.class);
    }


    @Test
    void testMockWithMocksName1(){
        MyList mockedMyList= Mockito.mock(MyList.class,"MyMOCKDLIST");
        Mockito.when(mockedMyList.add(Mockito.anyString())).thenReturn(false);

        Boolean added= mockedMyList.add(randomAlphabetic(6));

        MatcherAssert.assertThat(added, CoreMatchers.is(false));

        // wanted 2 times but was 1 times so exceptions thrown :: org.mockito.exceptions.verification.TooFewActualInvocations:
        //Mockito.verify(mockedMyList, Mockito.times(2)).add(Mockito.anyString());

        assertThatThrownBy(() -> Mockito.verify(mockedMyList, Mockito.times(2)).add(Mockito.anyString()))
                .isInstanceOf(TooFewActualInvocations.class)
                .hasMessageContaining("MyMOCKDLIST.add");

    }

    // Mocking With Answer
    // If we don’t set an expectation on a method, the default answer, configured by the CustomAnswer type, will come into play.
    // In order to prove this, we’ll skip over the expectation setting step and jump to the method execution:
    @Test
    void testMockWithAnswerBoolean(){
        MyList listMock = Mockito.mock(MyList.class, new CustomAnswer());
        boolean added = listMock.add(randomAlphabetic(6));
        Mockito.verify(listMock).add(Mockito.anyString());
        MatcherAssert.assertThat(added,CoreMatchers.is(false));
        Assertions.assertThat(added).isFalse();
    }

    // 5. Mocking With MockSettings
    @Test
    void testMockWithSettings(){
        MockSettings customSettings = Mockito.withSettings().defaultAnswer(new CustomAnswer());
        MyList listMock = Mockito.mock(MyList.class, customSettings);
        boolean added = listMock.add(randomAlphabetic(6));

        Mockito.verify(listMock).add(Mockito.anyString());
        Assertions.assertThat(added).isFalse();
    }

    //===================================================================================================================


    // Verify the number of interactions with mock
    @Test
    void testVerifyWithNumberOfInteractions(){
        MyList mockedMyList= Mockito.mock(MyList.class);
        mockedMyList.size();
        Mockito.verify(mockedMyList,Mockito.times(1)).size();
    }

    // Verify no interaction with the whole mock occurred:
    @Test
    void testVerifyNoInteraction(){
        MyList mockedMyList= Mockito.mock(MyList.class);
        Mockito.verifyNoInteractions(mockedMyList);
    }

    // Verify no interaction with a specific method occurred:
    // Verify an interaction has not occurred:
    @Test
    void testVerifyNoInteractionWithSpecificMethod(){
        MyList mockedMyList= Mockito.mock(MyList.class);
        mockedMyList.add(randomAlphabetic(7));
        Mockito.verify(mockedMyList,Mockito.times(0)).size();
        Mockito.verify(mockedMyList,Mockito.never()).size();
    }

    // Verify there are no unexpected interactions — this should fail:
    @Test
    void testVerifyNoUnExcpectedInteractions(){
        MyList mockedList = Mockito.mock(MyList.class);
        mockedList.size();
        mockedList.clear();

        Mockito.verify(mockedList).size();
        //Mockito.verify(mockedList).clear();

        // this verify will  throw this Exception org.mockito.exceptions.verification.NoInteractionsWanted:
        // as after size() is verified clear() also has to be verified but as clear() has not verified using verifyNoMoreInteractions() will throw exception
        //Mockito.verifyNoMoreInteractions(mockedList);

        //since clear() is also there using verifyNoMoreInteractions() will throw exception NoInteractionsWanted.class which is asserted
        org.junit.jupiter.api.Assertions.assertThrows(NoInteractionsWanted.class, () -> Mockito.verifyNoMoreInteractions(mockedList));
    }

    // Verify the order of interactions:
    @Test
    void testVerifyOrderOfInteractions(){
        List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.size();
        mockedList.add("a parameter");
        mockedList.clear();

        InOrder inOrder = Mockito.inOrder(mockedList);
        // // even if this statement is commented but still order is following i.e. clear() after add() and size() in first
        inOrder.verify(mockedList).size();
        inOrder.verify(mockedList).add("a parameter");
        inOrder.verify(mockedList).clear();

        // This is off the order of execution or interaction so exception will be thrown
        // :: org.mockito.exceptions.verification.VerificationInOrderFailure:
        //inOrder.verify(mockedList).size();
    }

    // Verify an interaction has occurred at least a certain number of times:
    @Test
    void testVerifyInteractionOccuranceAtLeastCertainNumberOfTimes(){
        List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.clear();
        mockedList.clear();
        mockedList.clear();mockedList.clear();
        mockedList.clear();
        mockedList.clear();mockedList.clear();
        mockedList.clear();
        mockedList.clear();mockedList.clear();
        mockedList.clear();
        mockedList.clear();

        Mockito.verify(mockedList, Mockito.atLeast(1)).clear();

        // org.mockito.exceptions.verification.MoreThanAllowedActualInvocations:
        // Wanted at most 10 times but was 12
        Mockito.verify(mockedList, Mockito.atMost(10)).clear();
    }

    // Verify interaction with the exact argument:
    @Test
    void testVerifyWithExactArgument(){
        List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.add("test");

        // if the argument does't match that passed above while adding then exception is thrown
        Mockito.verify(mockedList).add("test");
    }

    // Verify interaction with flexible/any argument:
    @Test
    void testVerifyWithFlexibleArgument(){
        List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.add("test");

        Mockito.verify(mockedList).add(Mockito.anyString());
    }

    // Verify interaction using argument capture:
    // we can use an ArgumentCaptor with stubbing, we should generally avoid doing so this means avoiding using
    // an ArgumentCaptor with Mockito.when. With stubbing, we should use an ArgumentMatcher instead.
    @Test
    void testVerifyWithArgumentCapture(){
        List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.addAll(Lists.<String> newArrayList("someElement1", "someElement2", "someElement3"));

        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockedList).addAll(argumentCaptor.capture());

        List<String> capturedArgument = argumentCaptor.getValue();
        System.out.println(capturedArgument);
        System.out.println(argumentCaptor.getAllValues());
        Assertions.assertThat(capturedArgument).contains("someElement1").contains("someElement2","someElement3");
    }

    // =================================================================================================================

    // Configure simple return behavior for mock:

    @Test
    void testSimpleReturnBehavior(){
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(Mockito.anyString())).thenReturn(false);

        boolean added = listMock.add(randomAlphabetic(6));
        Assertions.assertThat(added).isFalse();
    }

    // Configure return behavior for mock in an alternative way:
    @Test
    void testAlternativeReturnBehavior(){
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.doReturn(false).when(listMock).add(Mockito.anyString());

        boolean added = listMock.add(randomAlphabetic(6));
        Assertions.assertThat(added).isFalse();
    }

    // Configure mock to throw an exception on a method call:
    @Test
    void testThrowExceptionWithNonVoidReturnTypeMethod(){
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(Mockito.anyString())).thenThrow(IllegalStateException.class);


        // Noncompliant code example
        // Act and Assert using assertThrows
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> listMock.add(randomAlphabetic(6)));

        // Compliant solution
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> listMock.add(randomAlphabetic(6)));
        MatcherAssert.assertThat(exception, CoreMatchers.instanceOf(IllegalStateException.class));


        try{
            listMock.add(randomAlphabetic(7));
        }catch (IllegalStateException e){
            MatcherAssert.assertThat(e,CoreMatchers.is(CoreMatchers.instanceOf(IllegalStateException.class)));
            MatcherAssert.assertThat(e.getMessage(),Matchers.is(Matchers.nullValue()));
        }

    }



    // Configure the behavior of a method with void return type — to throw an exception:
    @Test
    void testVoidReturnType(){
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.doThrow(NullPointerException.class).when(listMock).clear();

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> listMock.clear());

    }

    // Configure the behavior of multiple calls:
    @Test
    void testMultipleCalls(){
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(Mockito.anyString()))
                .thenReturn(false)
                .thenThrow(IllegalStateException.class);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            listMock.add(randomAlphabetic(6));
            listMock.add(randomAlphabetic(6));
        });
    }


    // Configure the behavior of a spy:
    @Test
    void testBehaviorSpy(){
        MyList instance = new MyList();
        MyList spy = Mockito.spy(instance);

        Mockito.doThrow(NullPointerException.class).when(spy).size();

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> spy.size());
    }

    //Configure method to call the real, underlying method on a mock:
    @Test
    void testCallRealMethod(){
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.size()).thenCallRealMethod();

        Assertions.assertThat(listMock).hasSize(1);
    }

    // Configure mock method call with custom Answer:
    @Test
    void testCustomAnswer(){
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.doAnswer(invocation -> "Always the same").when(listMock).get(Mockito.anyInt());

        String element = listMock.get(1);
       Assertions.assertThat(element).isEqualTo("Always the same");
    }
}
