package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.callback_test1;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(String errorMessage);
}