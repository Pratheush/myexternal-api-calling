package org.dailycodebuffer.codebufferspringbootmongodb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class MyFactorialTest {
    /*private Integer input;
    private Integer expected;*/


    // Constructor Injection
    /*public MyFactorialTest(Integer input, Integer expected){
        this.input = input;
        this.expected = expected;
    }*/

    // for parameterized injection parameters should be public not private
    @Parameterized.Parameter(0)
    public Integer input;
    @Parameterized.Parameter(1)
    public Integer expected;

    @Parameterized.Parameters(name="{index} - MyFactorial({0}) - {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 1},{1,1},{2,2},{3,6},{4,24},{5,120}
        });
    }

    @Test
    public void parametrizedFactorialTest(){
        MyFactorialTest.data().stream().flatMap(array -> Arrays.stream(array).map(obj -> (Integer) obj)).forEach(System.out::println);
        assertThat(expected,is(equalTo(MyFactorial.compute(input))));
    }
}


