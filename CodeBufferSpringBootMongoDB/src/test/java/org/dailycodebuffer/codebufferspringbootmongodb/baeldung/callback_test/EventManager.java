package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.callback_test;

public class EventManager {
    private Callback callback;

    public void registerCallback(Callback callback) {
        this.callback = callback;
    }

    public void triggerEvent() {
        if (callback != null) {
            callback.onEvent();
        }
    }
}