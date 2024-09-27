package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.abstract_class;

import org.junit.Test;



import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AbstractPrivateMethods.class})
public class AbstractClassUsingPrivateMethodTest {

    @Test
    public void whenMockPrivateMethod_thenVerifyBehaviour() throws Exception {
        AbstractPrivateMethods mockClass = PowerMockito.mock(AbstractPrivateMethods.class);
        PowerMockito.doCallRealMethod()
                .when(mockClass)
                .defaultImpl();

        String dateTime = LocalDateTime.now().toString();
        PowerMockito.doReturn(dateTime).when(mockClass, "getCurrentDateTime");

        String actual = mockClass.defaultImpl();

        assertEquals(dateTime + "DEFAULT-1", actual);

        PowerMockito.verifyPrivate(mockClass).invoke("getCurrentDateTime");
    }
}
