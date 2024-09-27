package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.abstract_class;

/**
 *  testing abstract classes should almost always go through the public API of the concrete implementations
 *  Powermock isn’t fully supported for Junit5.
 *
 *  to test the method defaultImpl(), and we have two possible solutions – using a concrete class, or using Mockito.
 *
 *  (1). Using a Concrete Class
 *  Create a concrete class which extends AbstractIndependent class, and use it to test the method:
 *  The drawback of this solution is the need to create the concrete class with
 *  dummy implementations of all abstract methods.
 *
 *  public class ConcreteImpl extends AbstractIndependent {
 *
 *     @Override
 *     public int abstractFunc() {
 *         return 4;
 *     }
 * }
 *
 * @Test
 * public void givenNonAbstractMethod_whenConcreteImpl_testCorrectBehaviour() {
 *     ConcreteImpl conClass = new ConcreteImpl();
 *     String actual = conClass.defaultImpl();
 *
 *     assertEquals("DEFAULT-1", actual);
 * }
 *
 *
 *
 *(2). Using Mockito
 * The most important part here is the preparation of the mock
 * to use the real code when a method is invoked using Mockito.CALLS_REAL_METHODS.
 * @Test
 * public void givenNonAbstractMethod_whenMockitoMock_testCorrectBehaviour() {
 *     AbstractIndependent absCls = Mockito.mock(
 *       AbstractIndependent.class,
 *       Mockito.CALLS_REAL_METHODS);
 *
 *     assertEquals("DEFAULT-1", absCls.defaultImpl());
 * }
 *================================================================================================
 *
 * (2). Abstract Method Called From Non-Abstract Method
 * In this case, the non-abstract method defines the global execution flow,
 * while the abstract method can be written in different ways depending upon the use case:
 *
 * To test this code, we can use the same two approaches as before –
 * either create a concrete class or use Mockito to create a mock:
 *
 * public abstract class AbstractMethodCalling {
 *
 *     public abstract String abstractFunc();
 *
 *     public String defaultImpl() {
 *         String res = abstractFunc();
 *         return (res == null) ? "Default" : (res + " Default");
 *     }
 * }
 *
 * Here, the abstractFunc() is stubbed with the return value  and
 * when we call the non-abstract method defaultImpl(), it will use this stub.
 *
 * @Test
 * public void givenDefaultImpl_whenMockAbstractFunc_thenExpectedBehaviour() {
 *     AbstractMethodCalling cls = Mockito.mock(AbstractMethodCalling.class);
 *     Mockito.when(cls.abstractFunc())
 *       .thenReturn("Abstract");
 *     Mockito.doCallRealMethod()
 *       .when(cls)
 *       .defaultImpl();
 *
 *     assertEquals("Abstract Default", cls.defaultImpl());
 * }
 *
 * =================================================================================================================
 *
 * (3). Non-Abstract Method With Test Obstruction
 *  the method we want to test calls a private method which contains a test obstruction.
 *  We need to bypass the obstructing test method before testing the target method:
 *
 *  public abstract class AbstractPrivateMethods {
 *
 *     public abstract int abstractFunc();
 *
 *     public String defaultImpl() {
 *         return getCurrentDateTime() + "DEFAULT-1";
 *     }
 *
 *     private String getCurrentDateTime() {
 *         return LocalDateTime.now().toString();
 *     }
 * }
 *
 * the defaultImpl() method calls the private method getCurrentDateTime() and gets the current time at runtime.
 * which should be avoided in our unit tests
 * we cannot use mockito to mock the standard behavior of this private method because mockito cannot control private methods.
 * Thus we need to use PowerMock.
 *
 * @RunWith defines PowerMock as the runner for the test
 * @PrepareForTest(class) tells PowerMock to prepare the class for later processing
 *
 * PowerMock to stub the private method getCurrentDateTime().
 * PowerMock will use reflection to find it because it’s not accessible from outside.
 * So, when we call defaultImpl(), the stub created for a private method will be invoked instead of the actual method.
 *
 * @RunWith(PowerMockRunner.class)
 * @PrepareForTest(AbstractPrivateMethods.class)
 * public class AbstractPrivateMethodsUnitTest {
 *
 *     @Test
 *     public void whenMockPrivateMethod_thenVerifyBehaviour() {
 *         AbstractPrivateMethods mockClass = PowerMockito.mock(AbstractPrivateMethods.class);
 *         PowerMockito.doCallRealMethod()
 *           .when(mockClass)
 *           .defaultImpl();
 *         String dateTime = LocalDateTime.now().toString();
 *         PowerMockito.doReturn(dateTime).when(mockClass, "getCurrentDateTime");
 *         String actual = mockClass.defaultImpl();
 *
 *         assertEquals(dateTime + "DEFAULT-1", actual);
 *     }
 * }
 *
 * =================================================================================================================
 * (4). Non-Abstract Method Which Accesses Instance Fields
 * Abstract classes can have an internal state implemented with class fields.
 * The value of the fields could have a significant effect on the method getting tested.
 *
 * If a field is public or protected, we can easily access it from the test method.
 * * But if it’s private, we have to use PowerMockito:
 *
 * Here, the testFunc() method is using instance-level fields count and active before it returns
 * When testing testFunc(), we can change the value of the count field by accessing instance created using Mockito.
 * but to test the testFunc() method which is using private instance variable we have to use PowerMockito and its Whitebox class:
 * we’re using Whitebox class to control object’s internal state. The value of the active field is changed to true.
 *
 *public abstract class AbstractInstanceFields {
 *     protected int count;
 *     private boolean active = false;
 *
 *     public abstract int abstractFunc();
 *
 *     public String testFunc() {
 *         if (count > 5) {
 *             return "Overflow";
 *         }
 *         return active ? "Added" : "Blocked";
 *     }
 * }
 *
 *
 *
 *
 * public class AbstractInstanceFieldTest {
 *
 *     @Test
 *     public void whenPowerMockitoAndActiveFieldTrue_thenCorrectBehaviour() {
 *         AbstractInstanceFields instClass = PowerMockito.mock(AbstractInstanceFields.class);
 *         PowerMockito.doCallRealMethod()
 *                 .when(instClass)
 *                 .testFunc();
 *         Whitebox.setInternalState(instClass, "active", true);
 *
 *         assertEquals("Added", instClass.testFunc());
 *     }
 *
 *     @Test
 *     public void whenPowerMockitoAndActiveFieldTrue_thenCorrectBehaviour2() {
 *         AbstractInstanceFields instClass = PowerMockito.mock(AbstractInstanceFields.class);
 *         PowerMockito.doCallRealMethod()
 *                 .when(instClass)
 *                 .testFunc();
 *         Whitebox.setInternalState(instClass, "active", false);
 *
 *         assertEquals("Blocked", instClass.testFunc());
 *     }
 *
 *     @Test
 *     public void whenPowerMockitoAndActiveFieldTrue_thenCorrectBehaviour3() {
 *         AbstractInstanceFields instClass = PowerMockito.mock(AbstractInstanceFields.class);
 *         PowerMockito.doCallRealMethod()
 *                 .when(instClass)
 *                 .testFunc();
 *         Whitebox.setInternalState(instClass, "count", 6);
 *
 *         assertEquals("Overflow", instClass.testFunc());
 *     }
 *
 *     @Test
 *     public void whenPowerMockitoAndActiveFieldTrue_thenCorrectBehaviour4() {
 *         AbstractInstanceFields instClass = PowerMockito.mock(AbstractInstanceFields.class);
 *         PowerMockito.doCallRealMethod()
 *                 .when(instClass)
 *                 .testFunc();
 *         //Whitebox.setInternalState(instClass, "count", 6);
 *         instClass.count=6;
 *
 *         assertEquals("Overflow", instClass.testFunc());
 *     }
 * }
 *
 *
 */
public abstract class AbstractIndependent {
    public abstract int abstractFunc();

    public String defaultImpl() {
        return "DEFAULT-1";
    }
}
