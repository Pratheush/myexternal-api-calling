package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_advance;

import java.util.AbstractList;

public class MyList1 extends AbstractList<String> {

    @Override
    public String get(int index) {
        return null;
    }

    @Override
    public void add(int index, String element) {
        // no-op
    }

    @Override
    public int size() {
        return 0;
    }
}
