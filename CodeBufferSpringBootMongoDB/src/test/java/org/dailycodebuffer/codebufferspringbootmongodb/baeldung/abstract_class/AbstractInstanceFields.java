package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.abstract_class;

public abstract class AbstractInstanceFields {
    protected int count;
    private boolean active = false;

    public abstract int abstractFunc();

    public String testFunc() {
        if (count > 5) {
            return "Overflow";
        }
        return active ? "Added" : "Blocked";
    }
}
