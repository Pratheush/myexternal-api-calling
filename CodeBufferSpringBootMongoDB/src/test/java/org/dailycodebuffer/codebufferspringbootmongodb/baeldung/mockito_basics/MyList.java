package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_basics;

import java.util.AbstractList;

public class MyList extends AbstractList<String> {
    @Override
    public String get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void add(int index, String element) {
        // no-op
    }

}
