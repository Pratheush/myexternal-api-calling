package org.dailycodebuffer.codebufferspringbootmongodb;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.CombinableMatcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForClassTypes.not;
// import static org.hamcrest.CoreMatchers.equalTo;
// import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class MyAssertionTest {

    private static String message() {
        return "TEST Execution Failed :: ";
    }

    // assumeTrue
    //If the condition in assumeTrue is true then only the rest of the test method is executed, else the test is skipped.
    @Test
    void testAssumeTrueOnTestEnv() {
        System.setProperty("ENV", "TEST");
        assumeTrue("TEST".equals(System.getProperty("ENV")));
        // Since the condition is true rest of it will get executed
        assertTrue(Math.random() > 0);
    }
    @Test
    void testAssumeTrueOnTestEnvWithFalseConditionWithMessageSupplier() {
        System.setProperty("ENV", "TEST");
        assumeTrue("PRODUCTION".equals(System.getProperty("ENV")), MyAssertionTest::message);
        // Since the condition is false rest of it will NOT get executed
        assertTrue(Math.random() > 0);
    }

    // assumeFalse
    //If the condition in assumeFalse is false then only the rest of the test method is executed, else the test is skipped.
    @Test
    void testAssumeFalseWithFalseCondition() {
        System.setProperty("ENV", "PRODUCTION");
        assumeFalse("TEST".equals(System.getProperty("ENV")));
        // Since the condition is false rest of it will get executed
        assertTrue(Math.random() > 0);
    }
    @Test
    void testAssumeFalseWithTrueConditionWithMessageSupplier() {
        System.setProperty("ENV", "PRODUCTION");
        assumeFalse("PRODUCTION".equals(System.getProperty("ENV")), MyAssertionTest::message);
        // Since the condition is True rest of it will not get executed
        assertTrue(Math.random() > 0);
    }

    // assumingThat runs the executable only if the condition is true.
    // if the condition is false, then it just skips the executable block and the rest of the test method is still executed.
    // Let’s implement a test using assumingThat method with the true condition so that the executable executes.
    @Test
    void testAssumingThat() {
        System.setProperty("ENV", "CI");
        assumingThat(
                "CI".equals(System.getProperty("ENV")),
                () -> {
                    // run the end2end test cases
                    System.out.println("Assuming that executable executed");
                });
        // Since the condition is false rest of it will get executed
        assertTrue(Math.random() > 0,MyAssertionTest::message);
    }


    @Test
    void testAssertArrayEquals(){
        // Assert.assertArrayEquals(expectedArray, actualArray);
        //The assertArrayEquals() method in Java is used to compare two arrays.
        // It takes two arrays as input and returns true if the arrays are equal, false otherwise.
        // The arrays are considered equal if they have the same length and the same elements in the same order.
        byte[] expected="Trial".getBytes();
        byte[] result="Trial".getBytes();
        assertArrayEquals(expected, result,()-> "TEST Execution Failed ::");
    }

    @Test
    void testAssertEquals(){
        // assertEquals() methods checks that the two objects are equals or not.
        String string1 = "test";
        String string2 = "test";
        String string3 = new String("test");

        assertEquals(string1,string2,MyAssertionTest::message);
        assertEquals(string1,string3,"AssertEquals Test Failed");
    }

    @Test
    void testAssertNotEquals(){
        /*
        The assertNotEquals() method in Java is used to assert that two values are not equal.
        It takes two arguments: the expected value and the actual value.
        If the expected value is equal to the actual value, an AssertionError is thrown.
         */
        String string1 = "test";
        String string2 = "Demo";
        String string3 = new String("Demo1");
        assertNotEquals(string1,string2,"assertNotEquals Failed");
        assertNotEquals(string2,string3,MyAssertionTest::message);
    }

    @Test
    void testAssertFalse(){
        // assertFalse(boolean condition) asserts that the supplied condition is false .
        BooleanSupplier booleanSupplier = ()-> 5%2==0;
        Supplier<String> messageSupplier = ()-> "supplied booleanSupplier is not even i.e why test will pass";
        assertFalse(booleanSupplier.getAsBoolean(),messageSupplier);
    }

    @Test
    void testAssertTrue(){
        // assertTrue("Success - should be true", false);
        // assertTrue(boolean condition) asserts that the supplied condition is true .
        BooleanSupplier booleanSupplier=()-> true;
        assertTrue(booleanSupplier.getAsBoolean(),MyAssertionTest::message);
    }

    @Test
    void testAssertNull(){
        //assertNull("should be null", null);
        // The assertNull() method means "a passed parameter must be null ": if it is not null then the test case fails.
        Integer i=null;
        assertNull(i,MyAssertionTest::message);
    }

    @Test
    void testAssertNotNull(){
        // assertNotNull("should not be null", new Object());
        // assertNotNull asserts that the object is not null. If it is null the test fails,
        List<String> strList=List.of("Not Null");
        assertNotNull(strList,MyAssertionTest::message);

        String myString = "Hello, World!";
        assertNotNull(myString);
    }

    @Test
    void testAssertSame(){
        // The assertSame() method in Java is used to test if two object references point to the same object.
        // assertSame("should be same", aNumber, aNumber);
        String string1 = "test";
        String string2 = "test";

        // This will pass because string1 and string2 refer to the same object.
        assertSame(string1, string2, MyAssertionTest::message);

        String string3 = new String("Test");

        // This will fail because string1 and string3 do not refer to the same object.
        assertSame(string1, string3);
    }

    @Test
    void testAssertNotSame(){
        // The assertNotSame() method in Java is used to verify that two objects do not refer to the same object.
        String str=new String("Hello, World!");
        String str1=new String("Hello, World!");

        // This will pass because str and str1 do not refer to the same object.
        assertNotSame(str,str1,MyAssertionTest::message);

        String string1 = "Hello";
        String string2 = new String("Hello");

        // This will pass because string1 and string2 do not refer to the same object.
        assertNotSame(string1, string2);
    }

    @Test
    @DisplayName("Assert Test assertIterableEquals")
    void testAssertIterableEquals(){
        // The assertIterableEquals() method in Java is used to assert that two iterables are equal.
        // The iterables can be of any type, but they must contain the same elements in the same order.
        List<Integer> intListOne=IntStream.rangeClosed(1,7).boxed().toList();
        List<Integer> intListTwo=IntStream.rangeClosed(1,7).boxed().toList();
        List<Integer> intListThree=IntStream.rangeClosed(1,7).boxed().sorted(Comparator.reverseOrder()).toList();

        assertIterableEquals(intListOne,intListTwo,"assertIterableEquals Failed");
       // assertIterableEquals(intListOne,intListThree,"Second assertIterableEquals Failed"); // this test will since iterable contents are differ at index
    }

    @Test
    @DisplayName("Assert Exception Test")
    void testAssertExceptions(){
        // The assertThrows() will PASS: If the code block throws an exception of the specified type or a subtype.
        // The assertThrows() method in Java is used to verify that a code block throws an exception of the expected type.
        // It takes two parameters: the type of exception you expect and an Executable.
        // The executable can be a Lambda expression, method reference, or implementation of the Executable interface.
        final Exception exception=assertThrows(ArithmeticException.class,()-> Calculator.divide(5,0),"assertThrows Exception ");
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("This is an illegal argument");
        });

        assertThat(exception.getMessage(),CoreMatchers.containsStringIgnoringCase("/ by zero"));
    }

    @Test
    @DisplayName("assertAll Tests")
    void testAssertAll(){
        // The assertAll() method is a part of JUnit 5 and is used to group multiple assertions within a test method.
        // It takes a collection of Executable objects as its parameter and asserts that all of them do not throw an AssertionError.
        // The assertDoesNotThrow method in Java is used to assert that a code block does not throw an exception.
        assertAll(
                () -> assertEquals(3,Calculator.add(1,2)),
                () -> assertEquals(1,Calculator.add(-1,2)),
                () -> assertEquals(-3,Calculator.add(-1,-2)),
                () -> assertEquals(2,Calculator.add(0,2)),
                () -> assertThrowsExactly(ArithmeticException.class,()->Calculator.divide(0,0)),
                () -> assertDoesNotThrow(()->Calculator.divide(1,2))

        );

        // below test using testNG dependency::
       /* // Create a SoftAssert object
        SoftAssert softAssert = new SoftAssert();

        // Add multiple assertions to the SoftAssert object
        softAssert.assertEquals(1, 1);
        softAssert.assertTrue(true);
        softAssert.assertFalse(false);

        // Call assertAll() to verify that all assertions passed
        softAssert.assertAll();*/

    }


    //The @Disabled annotation is used to disable or skip tests at class or method level.
    @Disabled
    @Test
    @DisplayName("testing Assertions Fail")
    void testAssertionsFail(){
        // The Assertions.fail method in Java is used to fail a test unconditionally.
        // It is typically used to explicitly create a failure under desired testing conditions.
        Assertions.fail("Assertions.fail() has failed");
    }

    @Test
    void testAssertThat(){
        //  Core Hamcrest Matchers with assertThat
        // AssertThat is a method in Java that is used to test the correctness of code.
        // It is used to check if the output of a method is as expected.
        // There are many examples of AssertThat in Java, such as asserting that the
        // toString method of an Object returns a specified String1,
        // asserting that a List contains certain elements2, and asserting that a Map contains certain key-value pairs

        // following code uses assertThat() to assert that the actual value is equal to the expected value:
        // assertThat(actual, is(equalTo(expected))); ::: assertThat(actual,  org.hamcrest.Matcher<? super T> matcher)
        String str="Hello World";
        String strUp=str.toUpperCase();
        assertThat(strUp, is(equalTo(str.toUpperCase())));

        // assertThat(String reason, actual, is(not(equalTo(expected))));
        // following code uses assertThat() to assert that the actual value is not equal to the expected value:
        // If the values are equal, the test case will fail. If the values are not equal, the test case will pass.
        // assertThat(actual, is(not(equalTo(expected))));
        // Assume we have an actual value
        String actual = "Hello";

        // Assume we expect a value different from "World"
        String expected = "World";

        // Making the assertion
        assertThat("The actual value should not be equal to 'World'", actual,
                CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(expected))));

        String actual1 = "Hello";
        String expected1= "World";
        assertThat("The Hello are not equal To World", actual1,
                CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(expected1))));

        // following code uses assertThat() to assert that the actual value is greater than the expected value:
        // assertThat(actual, is(greaterThan(expected)));
        String name="Rahul Sharma";
        assertThat("name length is not greater than expected length", name.length(),
                CoreMatchers.is(Matchers.greaterThan(8)));

        // following code uses assertThat() to assert that the actual value is less than the expected value:
        // assertThat(actual, is(lessThan(expected)));
        assertThat("name length is not lessthan expected length", name.length(),
                CoreMatchers.is(Matchers.lessThan(15)));

        // following code uses assertThat() to assert that the actual value is a collection that contains the expected value:
        // assertThat(actual, contains(expected));
        List<Integer> intList=List.of(1,2,3,4,5,6,7);
        assertThat(intList,Matchers.containsInAnyOrder(7,6,5,4,3,2,1));
        // here order of items is important and to verify and assert matcher should contains all the items of the list to test
        assertThat(intList,Matchers.contains(1,2,3,4,5,6,7));

        assertThat("actual String > Effective Java  does not contains expected String >> Effective",
                "Effective Java",CoreMatchers.containsStringIgnoringCase("effective"));
        assertThat("Effective Java",
                CoreMatchers.both(CoreMatchers.containsString("Effective")).and(CoreMatchers.containsString("Java")));
        assertThat("Hello World Is My First Program",CoreMatchers.allOf(CoreMatchers.containsString("World"),CoreMatchers.containsString("First")));

        // It is used to assert if an actual object matches against all of the specified conditions
        // CoreMatchers.allOf() is used to assert that the testString object starts with "Achi", ends with "ul", and contains the string "Achilles"
        String testString = "Achilles is powerful";
        assertThat(testString, allOf(startsWith("Achi"), endsWith("ul"), containsString("Achilles")));

        // CoreMatchers.any() is used to verify that actual returns an object of type expected.
        // The any() method returns a matcher that matches any object of the given class,
        assertThat("Hello World",CoreMatchers.any(String.class));



        // following code uses assertThat() to assert that the actual value is a collection that does not contain the expected value:
        //assertThat(actual, not(contains(expected)));
        List<Integer> integerList= IntStream.range(10,15)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .toList();
        assertThat(integerList,Matchers.containsInAnyOrder(10,11,12,13,14));
        // this test will pass as it can take any order due to containsInAnyOrder but this has two times 12 due to which test will pass
        assertThat(integerList, CoreMatchers.not(Matchers.containsInAnyOrder(10,11,12,12,13,14)));

        assertTrue(integerList.stream().anyMatch(i->i.equals(13)),"assertTrue() and assertThat() same supposed to be is failing here");
        assertThat(integerList, CoreMatchers.hasItem(13));
        assertThat(integerList, CoreMatchers.hasItems(13,14,13)); //as long as item is in the integerList test will pass even if we pass expected item two times



        String s3="hello";
        String s4="hello";
        String s5=new String("hello");
        assertNotSame(s3,s5);
        assertThat(s3,CoreMatchers.sameInstance(s4));
        assertThat(s3,CoreMatchers.not(CoreMatchers.sameInstance(s5)));     // assertNotSame() is same as this inline statement of code does

        assertEquals(5,s3.length());
        assertThat(5,CoreMatchers.is(CoreMatchers.equalTo(s3.length()))); // assertEquals() do the same as this line of code does


        List<String> names=List.of("Rahul Sharma", "Ravi Kumar", "Manoj Tiwari","Manish Singh");
        assertThat(names,CoreMatchers.hasItem(CoreMatchers.startsWithIgnoringCase("ra")));
        assertThat(names,CoreMatchers.hasItem(CoreMatchers.endsWithIgnoringCase("gh")));
        assertThat(names,CoreMatchers.hasItem(CoreMatchers.startsWith("Ra")));
        assertThat(names,CoreMatchers.hasItem(CoreMatchers.endsWith("ma")));

        assertThat("Rahul Sharma",CoreMatchers.anyOf(CoreMatchers.startsWith("Ra"),CoreMatchers.endsWith("gh"))); // since anyOf either matcher has to be true for successful test

        assertThat("Lets Learn Java by Raj",CoreMatchers.not(CoreMatchers.anyOf(Matchers.containsString("Python"),Matchers.containsString("C++"))));
    }

    @Nested
    class CalculatorAdditionTest{
        @Test
        void testAddition1(){
            assertEquals(2,Calculator.add(1,1));
        }

        @Test
        void testAddition2(){
            assertEquals(-2,Calculator.add(-1,-1));
        }

        @Test
        void testAddition3(){
            assertEquals(-1,Calculator.add(-2,1));
        }

        // using reason or message inside assertThat or any other assert statement with lambda expression
        // then we call it lazy assert i.e. that supplier message will come when the assertion test fails
        @Test
        void testAddition4(){
            assertThat("testAddition4 Failed using assertThat input is 2 and -2 ",0,CoreMatchers.is(CoreMatchers.equalTo(Calculator.add(2,-2))));
        }
    }

    // Conditional Execution using @Enable conditions different types
    @EnabledForJreRange(min = JRE.JAVA_11,max= JRE.JAVA_22,disabledReason = "Older JRES are Not Supported")
    @EnabledOnOs(OS.WINDOWS)
    //@EnabledIf(value = "#{environment.acceptsProfiles('test')}" )
    @EnabledIf("#{systemProperties['os.name'].toLowerCase().contains('mac')}")
    @Test
    @DisplayName("Tesing Divide of Calculator")
    void testDivide(){
        assertThat(4,CoreMatchers.is(CoreMatchers.equalTo(Calculator.divide(24,6))));
    }


    // JUnit 5 has the ability to repeat a test a specified number of times simply by annotating a method with
    // @RepeatedTest and specifying the total number of repetitions desired.
    @RepeatedTest(value = 5, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("RepeatingTest")
    void customDisplayName(RepetitionInfo repInfo, TestInfo testInfo) {
        int i = 3;
        System.out.println(testInfo.getDisplayName());
        System.out.println(repInfo.getFailureCount());
        System.out.println(repInfo.getFailureThreshold());
        System.out.println(repInfo.getTotalRepetitions());
        System.out.println(testInfo.getTestClass());
        System.out.println(testInfo.getTestMethod());
        System.out.println(testInfo.getDisplayName() +
                "-->" + repInfo.getCurrentRepetition()
        );

        assertEquals(repInfo.getCurrentRepetition(), i);
    }

    // Parameterized tests make it possible to run a test multiple times with different arguments.
    // They are declared just like regular @Test methods but use the @ParameterizedTest
    // In addition, you must declare at least one source that will provide the arguments for each invocation
    // and then consume the arguments in the test method.
    // Here a parameterized test that uses the @ValueSource annotation to specify a String array as the source of arguments.
    @ParameterizedTest
    @ValueSource(strings ={"Master","Visa","Rupay","Platinum","Gold"})
    @Tag(value = "MyParameterizedTest")
    void testParametrized(String card,TestInfo testInfo){
        System.out.println("TAG :: "+testInfo.getTags());
        //assertThat(card.toLowerCase(),CoreMatchers.not(CoreMatchers.equalTo("rupay"))); //this test will fail when card value is rupay
        assertThat(card,CoreMatchers.not(CoreMatchers.equalTo("rupay"))); //this test will pass as card value rupay is expected but actual value is Rupay
    }


    // TO RUN Tag specific Tests just use mvn test -Dgroups="parametrized,MyParameterizedTest"
    @ParameterizedTest
    @Tag(value = "MyParameterizedTest")
    @ValueSource(strings = { "cali", "bali", "dani" })
    void endsWithI(String str) {
        assertTrue(str.endsWith("i"));
    }

    @Test
    void nonParametrizedFactorialTest(){
        assertThat(1,CoreMatchers.is(CoreMatchers.equalTo(MyFactorial.compute(0))));
        assertThat(1,CoreMatchers.is(CoreMatchers.equalTo(MyFactorial.compute(1))));
        assertThat(2,CoreMatchers.is(CoreMatchers.equalTo(MyFactorial.compute(2))));
        assertThat(6,CoreMatchers.is(CoreMatchers.equalTo(MyFactorial.compute(3))));
        assertThat(24,CoreMatchers.is(CoreMatchers.equalTo(MyFactorial.compute(4))));
        assertThat(120,CoreMatchers.is(CoreMatchers.equalTo(MyFactorial.compute(5))));
    }

    @Tag(value = "MyParameterizedTest")
    @ParameterizedTest(name="{index} - MyFactorial.compute({0})={1}") // {0} >> is input parameter and {1} >> is output parameter
    @CsvSource(value = {"0, 1", "1, 1", "2, 2", "3, 6", "4, 24", "5, 120"})
    void parametrizedFactorialTest(Integer input,Integer expected){
        assertThat(expected,is(equalTo(MyFactorial.compute(input))));
    }

    @Tag(value = "MyParameterizedTest")
    @ParameterizedTest(name="{index} - MyAdditiveTest({0},{1}) == {2}")
    @CsvSource(value = {"0,0,0" , "1,1,2", "3,2,5", "4,3,7"})
    void parameterizedAdditiveTest(Integer input1,Integer input2,Integer sum){
        assertThat(sum,is(equalTo(input1+input2)));
    }

    // Core Hamcrest Matchers with assertThat
    @Test
    void testAssertThatHamcrestCoreMatchers() {
        assertThat("good", allOf(equalTo("good"), startsWith("good")));
        assertThat("good", CoreMatchers.not(allOf(equalTo("bad"), equalTo("good"))));
        assertThat("good", anyOf(equalTo("bad"), equalTo("good")));
        assertThat(4, CombinableMatcher.<Integer> either(equalTo(3)).or(equalTo(4)));
        assertThat(new Object(), CoreMatchers.not(sameInstance(new Object())));
        assertThat("123",isA(String.class));
        assertThat("123",is("123"));
        assertThat("albumen", both(containsString("a")).and(containsString("b")));
        assertThat(Arrays.asList("one", "two", "three"), hasItems("one", "three"));
        assertThat(Arrays.asList(new String[] { "fun", "ban", "net" }), everyItem(containsString("n")));
    }
}
