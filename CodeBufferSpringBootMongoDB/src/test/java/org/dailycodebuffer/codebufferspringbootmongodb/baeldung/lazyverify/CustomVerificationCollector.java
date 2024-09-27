package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.lazyverify;

import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;

public class CustomVerificationCollector implements TestRule {
    private StringBuilder verificationFailures = new StringBuilder();

    public void collectVerificationFailure(String failureMessage) {
        verificationFailures.append(failureMessage).append("\n");
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } finally {
                    // Report failures after the test has run
                    if (verificationFailures.length() > 0) {
                        throw new AssertionError("Verification Failures:\n" + verificationFailures.toString());
                    }
                }
            }
        };
    }

    // Additional cleanup or reporting methods can be added if needed

    @After
    public void reportFailures() {
        if (verificationFailures.length() > 0) {
            System.out.println("Verification Failures:\n" + verificationFailures.toString());
        }
    }
}

