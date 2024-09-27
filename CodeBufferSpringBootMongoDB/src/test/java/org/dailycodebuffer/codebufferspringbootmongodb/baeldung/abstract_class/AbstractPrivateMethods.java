package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.abstract_class;

import java.time.LocalDateTime;

public abstract class  AbstractPrivateMethods {
    public abstract int abstractFunc();

    public String defaultImpl() {
        return getCurrentDateTime() + "DEFAULT-1";
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
}
