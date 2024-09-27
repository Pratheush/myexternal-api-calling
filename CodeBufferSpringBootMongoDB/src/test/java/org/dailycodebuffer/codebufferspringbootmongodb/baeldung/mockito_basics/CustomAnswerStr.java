package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_basics;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CustomAnswerStr implements Answer<String> {
    @Override
    public String answer(InvocationOnMock invocationOnMock) throws Throwable {
        return "My CustomAnswerStr Response";
    }
}
