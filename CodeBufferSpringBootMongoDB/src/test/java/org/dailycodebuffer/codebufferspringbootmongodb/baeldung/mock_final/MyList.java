package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mock_final;

import java.util.AbstractList;

public class MyList extends AbstractList<String> {
    final public int finalMethod() {
        return 0;
    }

    @Override
    public String get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
