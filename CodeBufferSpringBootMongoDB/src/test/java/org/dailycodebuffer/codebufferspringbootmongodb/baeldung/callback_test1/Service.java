package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.callback_test1;

public interface Service {
    void doAction(String request, Callback<Response> callback);
}