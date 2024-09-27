package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.mockito_basics;


import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DemoGettingStartedTest {

    @Mock
    private List<String> mockedlist;

    @Spy
    private List<String> spiedList=new ArrayList<>();

    @Captor
    ArgumentCaptor<String> argumentCaptorStr;

    @Mock
    Map<String,String> wordMap;
    MyDictionary spyDic;

    @BeforeEach
    public void init(){
        System.out.println("@BeforeEach init()");
        MockitoAnnotations.openMocks(this);
        spyDic=Mockito.spy(new MyDictionary(wordMap)); //instead of using annotation creating spy manually and injecting the mock through the constructor
    }

    @AfterEach
    public void destroy(){
        System.out.println("@AfterEach destroy()");
        mockedlist.clear();
        spiedList.clear();
        wordMap.clear();
    }

    /*@BeforeEach
    public void initialize(){
        MockitoAnnotations.openMocks(DemoGettingStartedTest.class);
    }*/

    @Test
    void whenNotUseMockAnnotation_thenCorrect(){
        // create a mock object of an ArrayList
        List<String> mockList= Mockito.mock(ArrayList.class);

        // Call the add() method on the mock object with parameter one
        //the add() method is called on the mock object, but since it is a mock object it does not have real implementation
        // therefore calling the add() method on the mock object will not actually add anything to the list.
        mockList.add("hello world");
        System.out.println("mockList size "+mockList.size());
        mockList.forEach((System.out::print));

        // Mockito.verify() is a method to check if a method of an object was called with certain parameters, number of times it was called
        // if add() was not called or called with wrong number of times or called with wrong parameters then test would fail
        // verify that add() method was called on the mock object with the parameter "hello world" number of times i.e. one
        Mockito.verify(mockList,Mockito.times(1)).add("hello world");

        System.out.println("mockList size "+mockList.size());
        mockList.forEach((System.out::print));
        assertEquals(0,mockList.size());

        when(mockList.size()).thenReturn(100);

        assertEquals(100,mockList.size());
    }

    @Test
    void whenUseMockAnnotation_thenMockIsInjected() {
        mockedlist.add("hello");
        Mockito.verify(mockedlist,Mockito.atLeastOnce()).add("hello");
        assertEquals(0,mockedlist.size());

        when(mockedlist.size()).thenReturn(100);
        assertEquals(100,mockedlist.size());
    }

    // @Spy Annotation
    @Test
    void whenNotUseSpyAnnotation_thenCorrect(){
        // Create a spy object of an ArrayList
        List<String> spyList=Mockito.spy(ArrayList.class);
        List<String> spyList1=Mockito.spy(new ArrayList<>());

        // call the add() method on the spy object with parameter "one" and "two"
        spyList.add("one");
        spyList.add("two");

        // verify that the add() method was called on the spy object with parameter "one" and "two"
        Mockito.verify(spyList,Mockito.times(1)).add("one");
        Mockito.verify(spyList,Mockito.times(1)).add("two");

        assertEquals(2,spyList.size());

        Mockito.doReturn(100).when(spyList).size();
        assertEquals(100,spyList.size());
    }

    @Test
    void testWhenUseSpyAnnotation_thenSpyIsInjectedCorrectly(){
        // Used the real method spiedList.add() to add elements to the spiedList
        spiedList.add("one");
        spiedList.add("two");

        Mockito.verify(spiedList,Mockito.atLeast(1)).add("one");
        Mockito.verify(spiedList,Mockito.times(1)).add("two");

        assertEquals(2,spiedList.size());

        // Stubbed the method spiedList.size() to return 100 instead of 2 using Mockito.doReturn()
        Mockito.doReturn(100).when(spiedList).size();
        assertEquals(100,spiedList.size());

    }

    // @Captor Annotation to create an ArgumentCaptor instance
    // test method to create an ArgumentCaptor without using the @Captor annotation
    @Test
    void whenNotUseCaptorAnnotation_thenCorrect(){
        List<String> mockList1=Mockito.mock(ArrayList.class);

        ArgumentCaptor<String> arg=ArgumentCaptor.forClass(String.class);

        mockList1.add("one");
        Mockito.verify(mockList1,Mockito.atMostOnce()).add(arg.capture());

        assertEquals("one",arg.getValue());

    }

    @Test
    void whenUseCaptorAnnotation_thenCorrect(){
        mockedlist.add("one");
        Mockito.verify(mockedlist,Mockito.atLeast(1)).add(argumentCaptorStr.capture());
        assertEquals("one", argumentCaptorStr.getValue());
    }

    // @InjectMocks annotation used to inject mock fields into the tested object automatically
    // @InjectMocks to inject the mock wordMap into the MyDictionary dic
    //@Mock
    //Map<String,String> wordMap;
    @InjectMocks
    MyDictionary dic=new MyDictionary();
    @Test
    void testMyDictionary_GetMeaning_WhenUseInjectMocksAnnotation_thenCorrect(){
        when(wordMap.get("aWord")).thenReturn("aMeaning");
        assertEquals("aMeaning",dic.getMeaning("aWord"));
    }

    // Injecting a Mock into a Spy i.e. @Mock Map<String,String> wordMap into MyDictionary
    //@Spy
    //MyDictionary spyDic=new MyDictionary();

    // Mockito doesn't support injecting mocks into spies and the following test results in an exception
    // if we want to use a mock with a spy we can manually inject the mock through a constructor
    /*
        public MyDictionary(Map<String,String> wordMap){
        this.wordMap = wordMap;
    }
    instead of using the annotation create spy manually
    @Mock
    Map<String,String> wordMap
    MyDictionary spyDic;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        spyDic=Mockito.spy(new MyDictionary(wordMap));
     }
     */
    @Test
    void testMyDictionary_GetMeaning_WhenUseSpyAnnotation_thenCorrect(){
        when(wordMap.get("aWord")).thenReturn("aMeaning");
        assertEquals("aMeaning",spyDic.getMeaning("aWord"));
    }

    @Test
    void givenNonVoidReturnType_whenUsingWhenThen_thenExceptionIsThrown(){
        MyDictionary dictMock=Mockito.mock(MyDictionary.class);
        when(dictMock.getMeaning(anyString())).thenThrow(NullPointerException.class);
        //when(dictMock.getMeaning(anyString())).thenThrow(new NullPointerException("Error occurred"));
        assertThrows(NullPointerException.class,() -> dictMock.getMeaning("word"));
    }

    @Test
    void givenVoidReturnType_whenUsingDo_thenCorrect(){
        MyDictionary dictMock=Mockito.mock(MyDictionary.class);
        AtomicBoolean verifyMethodCalled=new AtomicBoolean(false);
        AtomicReference<AtomicReferenceArray<Object>> args = new AtomicReference<>(new AtomicReferenceArray<>(new Object[0]));
        Mockito.doAnswer(invocationOnMock -> {
            args.set(new AtomicReferenceArray<>(invocationOnMock.getArguments()));

            verifyMethodCalled.set(true);
            return null;
        }).when(dictMock).add(anyString(), anyString());

        dictMock.add("word","meaning");

        //System.out.println(args.get().get(0).getClass());
        assertNotNull(args);
        assertThat(args.get().get(0), CoreMatchers.instanceOf(java.lang.String.class));
        assertThat(args.get().get(1), CoreMatchers.instanceOf(String.class));
        assertTrue(verifyMethodCalled.get());
    }

    @Test
    void givenNonVoidReturnType_whenUsingDoThrow_thenExceptionIsThrown(){
        MyDictionary dictMock=Mockito.mock(MyDictionary.class);
        Mockito.doThrow(NullPointerException.class).when(dictMock).getMeaning(anyString());
        assertThrows(NullPointerException.class,() -> dictMock.getMeaning("word"));
    }

    @Test
    void givenSpyAndNonVoidReturnType_whenUsingWhenThen_thenExceptionIsThrown(){
        MyDictionary spyDict=Mockito.spy(new MyDictionary());
        MyDictionary spyDict1=Mockito.spy(MyDictionary.class);

        Mockito.when(spyDict1.getMeaning(anyString())).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class,()->spyDict1.getMeaning("word"));

    }
}
