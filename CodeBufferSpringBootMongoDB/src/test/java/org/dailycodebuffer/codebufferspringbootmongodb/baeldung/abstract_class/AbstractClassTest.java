package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.abstract_class;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


class AbstractClassTest {

    // Using a Concrete Class
    @Test
     void givenNonAbstractMethod_whenConcreteImpl_testCorrectBehaviour() {
        ConcreteImpl conClass = new ConcreteImpl();
        String actual = conClass.defaultImpl();

        Assertions.assertEquals("DEFAULT-1", actual);
    }

    // Using Mockito
    @Test
    void givenNonAbstractMethod_whenMockitoMock_testCorrectBehaviour() {
        AbstractIndependent absCls = Mockito.mock(
                AbstractIndependent.class,Mockito.CALLS_REAL_METHODS);

        Assertions.assertEquals("DEFAULT-1", absCls.defaultImpl());
    }

    // =================================================================

    // using Concrete Class
    @Test
    void testAbstractMethodCalling_withconcreteClass() {
        AbstractMethodCallingImpl abscl=new AbstractMethodCallingImpl();
        Assertions.assertEquals("func Default", abscl.defaultImpl());
    }

    // Using Mockito
    @Test
    void testAbstractMethodCalling_withMockito(){
        AbstractMethodCalling abscl=Mockito.mock(AbstractMethodCalling.class,Mockito.CALLS_REAL_METHODS);
        Mockito.when(abscl.abstractFunc()).thenReturn("Abstract");
        Mockito.doCallRealMethod().when(abscl).defaultImpl();
        Assertions.assertEquals("Abstract Default",abscl.defaultImpl());
    }
}
