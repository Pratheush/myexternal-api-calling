package org.dailycodebuffer.codebufferspringbootmongodb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AdditionTest {

    private int firstSummand;

    private int secondSummand;

    private int sum;

    public AdditionTest(int firstSummand, int secondSummand, int sum) {
        this.firstSummand = firstSummand;
        this.secondSummand = secondSummand;
        this.sum = sum;
    }

    @Parameterized.Parameters(name = "{index} : {0} + {1} = {2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] { { 0, 0, 0 }, { 1, 1, 2 },
                { 3, 2, 5 }, { 4, 3, 7 } });
    }

    @Test
    public void testAdd() {
        assertEquals(sum, firstSummand + secondSummand);
    }
}
