package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
   often we may run into NullPointerException when we try to actually use the instance annotated with @Mock or @Spy
 */

@ExtendWith(MockitoExtension.class)
public class MockitoAnnotationUninitializedUnitTest {

    @Mock
    List<String> mockedList;

    //@Test(expected=NullPointerException.class)
    @Test
    void whenMockitoAnnotationUninitialized_thenNPEThrown(){
        Mockito.when(mockedList.size()).thenReturn(1);
        assertEquals(1, mockedList.size());
    }
}
