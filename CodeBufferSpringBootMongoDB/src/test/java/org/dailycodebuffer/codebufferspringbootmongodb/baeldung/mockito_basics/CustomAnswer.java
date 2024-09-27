package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_basics;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CustomAnswer implements Answer<Boolean> {

    @Override
    public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
        return false;
    }
}
