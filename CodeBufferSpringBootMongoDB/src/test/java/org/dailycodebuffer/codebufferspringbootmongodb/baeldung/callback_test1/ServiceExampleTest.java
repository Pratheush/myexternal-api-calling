package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.callback_test1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(MockitoExtension.class)
public class ServiceExampleTest {

    /**
     * since Callback is interface and it is been used in ServiceExample as dependency I created a Mocked-Object of Callback
     * But if Callback is class and as a dependency if it was used in ServiceExample then I have to create a mocked instance
     * of Callback and i have to do stubbing also for the specific method's return type accordingly.
     *
     * ArgumentCapture can be used while stubbing and verifying then we can assert the captured arguments
     *
     * creating the instance of a class for testing by using @InjectMocks or by just simply creating instance using constructor
     * then call the specific method on that instance which we want to perform test like here ::
     *  i created ServiceExample instance and then called doAction() method ( AS ACT )and inside doAction() implementation onSuccess()
     *  and onFailure() method is getting called by mocked-object which we will verify later and if necessary doing
     *  argument capturing and checking by asserting
     *
     *
     *  A callback is a piece of code that is passed as an argument to a method,
     *  which is expected to call back (execute) the argument at a given time.
     *
     * This execution may be immediate as in a synchronous callback,
     * but more typically it might happen at a later time as in an asynchronous callback.
     *
     */

    @Test
    public void testDoActionSuccess() {
        // Arrange
        ServiceExample serviceExample = new ServiceExample();
        Callback<Response> mockedCallback = Mockito.mock(Callback.class);
        String request = "Sample request";

        // Act
        serviceExample.doAction(request, mockedCallback);

        // Assert
        // Verify that the onSuccess method was called with the expected response
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        Mockito.verify(mockedCallback).onSuccess(responseCaptor.capture());

        // Perform additional assertions on the captured response if needed
        Response capturedResponse = responseCaptor.getValue();
         Assertions.assertEquals("Sample data", capturedResponse.getData(), "Assertion Failed  >> Not Sample data");

        // Verify that onFailure was not called
        //Mockito.verify(mockedCallback, Mockito.never()).onFailure(Mockito.anyString());
        Mockito.verify(mockedCallback, Mockito.atMostOnce()).onFailure(Mockito.anyString());
    }

    @Test
    public void testDoActionFailure() {
        // Arrange
        ServiceExample serviceExample = new ServiceExample();
        Callback<Response> mockedCallback = Mockito.mock(Callback.class);
        String request = "Sample request";

        // Act
        // Simulate a failure by uncommenting the onFailure call in the doAction method
         serviceExample.doAction(request, mockedCallback);

        // Assert
        ArgumentCaptor<String> stringArgumentCaptor=ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockedCallback).onFailure(stringArgumentCaptor.capture());

        Assertions.assertEquals("Request failed",stringArgumentCaptor.getValue(),"Assert Failed : Mismatch in msg");

        // Verify that onFailure was called with the expected error message
        //Mockito.verify(mockedCallback, Mockito.never()).onSuccess(Mockito.any());
        Mockito.verify(mockedCallback, Mockito.atMostOnce()).onSuccess(Mockito.any());

        // in above in this same test case method i already verified onFailure() and captured onFailure argument
        // for further assertion so verifying again onFailure() would fail the test case.
        //Mockito.verify(mockedCallback).onFailure("Expected error message");
    }


    // Using the doAnswer() Method
    // common solution for stubbing methods that have callbacks \
    // using Mockito’s Answer object and doAnswer method to stub the void method
    // we intercept the invocation and grab the method arguments using invocation.getArgument(1)
    @Test
    void testDoAnswer_OnSuccess(){

        ServiceExample mockCallback=Mockito.mock(ServiceExample.class);
        AtomicBoolean verifyMethodCalled=new AtomicBoolean(false);
        Response response=new Response();
        response.setData("First Simple Request");

        Mockito.doAnswer(invocationOnMock -> {
            String arg1=invocationOnMock.getArgument(0);
            Callback<Response> arg2=invocationOnMock.getArgument(1);
            arg2.onSuccess(response);
            String data = response.getData();
            org.assertj.core.api.Assertions.assertThat(data).isEqualTo("First Simple Request");
            verifyMethodCalled.set(true);
            return null;
        }).when(mockCallback).doAction(Mockito.anyString(),Mockito.any(Callback.class));

        ActionHandler actionHandler=new ActionHandler(mockCallback);
        actionHandler.doAction();

        Assertions.assertTrue(verifyMethodCalled.get());

        Mockito.verify(mockCallback).doAction(Mockito.anyString(),Mockito.any(Callback.class));
    }

    // Using an ArgumentCaptor
    // callbackCaptor.capture() as the second, which is where we capture the Callback object.
    @Test
    void testUsingArgumentCaptor(){
        // Arrange
        ServiceExample serviceExample=Mockito.mock(ServiceExample.class);
        ActionHandler actionHandler=new ActionHandler(serviceExample);
        ArgumentCaptor<Callback<Response>> callbackArgumentCaptor=ArgumentCaptor.forClass(Callback.class);

        // Act
        actionHandler.doAction();

        // Verify & Assert
        Mockito.verify(serviceExample).doAction(Mockito.anyString(), callbackArgumentCaptor.capture());

        Callback<Response> value = callbackArgumentCaptor.getValue();
        Response response = new Response();
        response.setData("Callback using ArgumentCaptor");
        value.onSuccess(response);

        String data = response.getData();
        String expectedResult="Callback using ArgumentCaptor";

        org.assertj.core.api.Assertions.assertThat(expectedResult).isEqualTo(data);

    }
}
