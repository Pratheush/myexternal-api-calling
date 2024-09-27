package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.abstract_class;

public abstract class AbstractMethodCalling {

    public abstract String abstractFunc();

    public String defaultImpl() {
        String res = abstractFunc();
        return (res == null) ? "Default" : (res + " Default");
    }
}