package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_advance;

import org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_basics.MyList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MockitoAdvanceTest {


    // we can use an ArgumentCaptor with stubbing, we should generally avoid doing so this means avoiding using
    // an ArgumentCaptor with Mockito.when. With stubbing, we should use an ArgumentMatcher instead.

    // Simple Mocking and Verifying
    // Void methods can be used with Mockito’s doNothing(), doThrow(), and doAnswer() methods, making mocking and verifying intuitive:
    // However, doNothing() is Mockito’s default behavior for void methods.

    @Test
    void testDoNothing(){
            MyList myList = Mockito.mock(MyList.class);
            Mockito.doNothing().when(myList).add(Mockito.isA(Integer.class), Mockito.isA(String.class));
            myList.add(0, "");

            Mockito.verify(myList, Mockito.times(1)).add(0, "");

    }

    // This version of whenAddCalledVerified() accomplishes the same thing as the one above:
    @Test
    void testDoNothing_AnotherVersion() {
        MyList myList = Mockito.mock(MyList.class);
        myList.add(0, "");

        Mockito.verify(myList, Mockito.times(1)).add(0, "");
    }


    @Test
    void testDoAnswer(){
        // Create a mock instance of MyList
        MyList mockedList = Mockito.mock(MyList.class);

        // Use doAnswer() to mock the behavior of the add() method
        Mockito.doAnswer((InvocationOnMock invocation) -> {
            // Access arguments passed to the add method
            int index = invocation.getArgument(0);
            String element = invocation.getArgument(1);

            // Perform any necessary logic (here, we're just printing for demonstration)
            System.out.println("Adding element '" + element + "' at index " + index);

            // You can perform additional logic or throw exceptions if needed
            return null; // For void methods, return null
        }).when(mockedList).add(Mockito.anyInt(), Mockito.anyString());

        // Call the add method on the mocked object
        mockedList.add(0, "MockedElement");

        // Verify that the add method was called with the specified arguments
        Mockito.verify(mockedList).add(Mockito.eq(0), Mockito.eq("MockedElement"));

        // You can also use ArgumentCaptor to capture the arguments and perform assertions
        ArgumentCaptor<Integer> indexCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> elementCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockedList).add(indexCaptor.capture(), elementCaptor.capture());

        // Perform assertions on captured values if needed
        System.out.println("Captured index: " + indexCaptor.getValue());
        System.out.println("Captured element: " + elementCaptor.getValue());
    }

    // doThrow() generates an exception:
    @Test
    void testDoThrow(){
        MyList myList = Mockito.mock(MyList.class);
        //Mockito.doThrow(UnfinishedStubbingException.class).when(myList).add(Mockito.isA(Integer.class), Mockito.isNull());
        Mockito.doThrow(IllegalArgumentException.class).when(myList).add(Mockito.isA(Integer.class), Mockito.isNull());

        try{
            myList.add(0,null);
        }catch (IllegalArgumentException e){
            MatcherAssert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(),CoreMatchers.is(Matchers.nullValue()));
        }

        Assertions.assertThrows(Exception.class, () -> {
            Mockito.doThrow().when(myList).add(Mockito.isA(Integer.class), Mockito.isNull());
        });
    }

    // Argument Capture
    // One reason to override the default behavior with doNothing() is to capture arguments.
    // we can use an ArgumentCaptor with stubbing, we should generally avoid doing so this means avoiding using
    // an ArgumentCaptor with Mockito.when. With stubbing, we should use an ArgumentMatcher instead.
    @Test
    void testDoNothingArgumentCaptureInBothArguments(){
        MyList myList = Mockito.mock(MyList.class);

        ArgumentCaptor<Integer> indexCapture=ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);

        Mockito.doNothing().when(myList).add(indexCapture.capture(),valueCapture.capture());

        myList.add(0,"capture element");

        MatcherAssert.assertThat("Captured Arguments Are Different",0,CoreMatchers.is(indexCapture.getValue()));
        MatcherAssert.assertThat("Captured Arguments Are Different",indexCapture.getValue(),CoreMatchers.isA(Integer.class));
        MatcherAssert.assertThat("Captured Arguments Are Different","capture element",CoreMatchers.is(valueCapture.getValue()));
        MatcherAssert.assertThat("Captured Arguments Are Different",valueCapture.getValue(),CoreMatchers.isA(String.class));
    }

    @Test
    void testDoNothingArgumentCaptureInOnlyOneArgumentCaputred(){
        MyList myList = Mockito.mock(MyList.class);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);

        Mockito.doNothing().when(myList).add(Mockito.anyInt(),valueCapture.capture());

        myList.add(0,"capture element");

        MatcherAssert.assertThat("Captured Arguments Are Different","capture element",CoreMatchers.is(valueCapture.getValue()));
        MatcherAssert.assertThat("Captured Arguments Are Different",valueCapture.getValue(),CoreMatchers.isA(String.class));
    }


    // Answering a Call to Void
    /*
        A method may perform more complex behavior than merely adding or setting value.
        For these situations, we can use Mockito’s Answer to add the behavior we need:
     */
    @Test
    void testDoAnswerCaptureBothArguments(){
        MyList myList = Mockito.mock(MyList.class);

        AtomicBoolean verifyMethodCalled = new AtomicBoolean(false);

        Mockito.doAnswer(invocationOnMock -> {
            Integer intIndex=invocationOnMock.getArgument(0);
            String strValue=invocationOnMock.getArgument(1);
            Assertions.assertEquals(2,intIndex);
            Assertions.assertEquals("doAnswer",strValue);
            verifyMethodCalled.set(true);
            return null;
        }).when(myList).add(Mockito.anyInt(),Mockito.anyString());

        myList.add(2,"doAnswer");

        Assertions.assertTrue(verifyMethodCalled.get());
    }

    // Mockito.doAnswer() can also be used to TEST CALLBACKS:::::::

    /*@Test
    public void givenServiceWithInvalidResponse_whenCallbackReceived_thenNotProcessed() {
        Response response = new Response();
        response.setIsValid(false);
        doAnswer((Answer<Void>) invocation -> {
            Callback<Response> callback = invocation.getArgument(1);
            callback.reply(response);
            Data data = response.getData();
            assertNull("No data in invalid response: ", data);
            return null;
        }).when(service).doAction(anyString(), any(Callback.class));
        ActionHandler handler = new ActionHandler(service);
        handler.doAction();
    }*/

    //Partial Mocking
    // Mockito’s doCallRealMethod() can be used for void methods:
    // This way, we can call the actual method and verify it at the same time.
    // calling a real method on a mock we may not get a very realistic behavior bcoz unlike spies,
    // mocked objects will skip all constructor and initializer calls including those to set fields. that means
    // if our method uses any instance state at all, it is unlikely to work as a mock with doCallRealMethod().
    @Test
    void testPartialMockingDoCallRealMethod() {
        MyList myList = Mockito.mock(MyList.class);

        Mockito.doCallRealMethod().when(myList).add(Mockito.anyInt(), Mockito.anyString());
        myList.add(1, "real");

        Mockito.verify(myList, Mockito.times(1)).add(1, "real");
    }



}
