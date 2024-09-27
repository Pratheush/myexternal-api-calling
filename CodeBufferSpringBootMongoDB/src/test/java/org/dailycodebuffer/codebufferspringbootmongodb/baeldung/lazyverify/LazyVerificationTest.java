package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.lazyverify;


import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.VerificationCollector;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;


class LazyVerificationTest {

    /**
     * This code declares a JUnit rule named initRule and annotates it with @Rule.
     * The rule is created using MockitoJUnit.rule(), which means it will set up Mockito for your test class.
     * Mockito is a popular mocking framework for Java that allows you to create mock objects for testing.
     *
     * With this rule in place, you can use Mockito features in your JUnit tests, such as creating mock objects,
     * stubbing methods, and verifying interactions with these mock objects.
     * The rule helps manage the lifecycle of Mockito mocks, ensuring proper initialization and cleanup for each test method.
     */
    /*@Rule
    public MockitoRule initRule = MockitoJUnit.rule();*/


    // Without VerificationCollector rule, only the first verification gets reported:
    //VerificationCollector is a JUnit rule which collects all verifications in test methods.
    //They’re executed and reported at the end of the test if there are failures:
    /*@Rule
    public VerificationCollector verificationCollector = MockitoJUnit.collector();*/

    @Rule
    public CustomVerificationCollector customVerificationCollector = new CustomVerificationCollector();

    @Test
    void testLazyVerification() throws Exception {
        List mockList = Mockito.mock(ArrayList.class);

        Mockito.verify(mockList).add("one");
        Mockito.verify(mockList).clear();
    }
}
