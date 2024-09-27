package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.callback_test;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class EventManagerTest {

    /**
     *  Testing callbacks with Mockito is a common scenario in Java development,
     *  especially when working with asynchronous or event-driven code.
     *
     *  a class EventManager that has a method registerCallback to register a callback function.
     *  The EventManager will then invoke the callback when a specific event occurs.
     *
     *   We'll mock the Callback interface and use it to verify that the callback method is called when triggerEvent is invoked.
     *
     *   Explanation:
     * 1. Mockito.mock(Callback.class): This creates a mock instance of the Callback interface.
     *
     * 2. eventManager.registerCallback(mockCallback): This registers the mock callback with the EventManager.
     *
     * 3. eventManager.triggerEvent(): This triggers the event in the EventManager, which should invoke the callback.
     *
     * 4. verify(mockCallback, times(1)).onEvent(): This verifies that the onEvent method of the mock callback was called exactly once.
     *
     * verifyNoMoreInteractions(mockCallback) to ensure that no other methods are called on the mock object.
     *
     * using ArgumentCaptor if you need to capture arguments passed to the callback for further verification.
     *
     */

    @Test
    public void testCallbackInvocation() {
        // Create a mock Callback
        Callback mockCallback = Mockito.mock(Callback.class);

        // Create an instance of EventManager
        EventManager eventManager = new EventManager();

        // Register the mock callback
        eventManager.registerCallback(mockCallback);

        // Trigger the event
        eventManager.triggerEvent();

        // Verify that the callback's onEvent method was called
        verify(mockCallback, times(1)).onEvent();
        verifyNoMoreInteractions(mockCallback);
    }
}