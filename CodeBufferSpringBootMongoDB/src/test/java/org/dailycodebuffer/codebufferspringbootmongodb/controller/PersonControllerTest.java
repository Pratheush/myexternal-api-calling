package org.dailycodebuffer.codebufferspringbootmongodb.controller;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.dailycodebuffer.codebufferspringbootmongodb.service.PersonService;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // ENABLE MOCKITO ANNOTATIONS this is one way of enabling mockito annotations
public class PersonControllerTest {

    static List<Person> personList=new ArrayList<Person>();

    @BeforeAll
    public static void setPersonList(){
       personList= IntStream.rangeClosed(1,15)
               .mapToObj(count -> {

                   List<String> hobbyList = new ArrayList<>();
                   hobbyList.add("Coding");
                   hobbyList.add("Watching Kdramas");
                   hobbyList.add("Playing Chess");

                   List<Address> addressList = new ArrayList<>();
                   addressList.add(Address.builder().address1("Kerala"+1).address2("Kochi"+1).city("Ernakulam"+1).build());
                   addressList.add(Address.builder().address1("Karnataka"+2).address2("Marathahalli"+2).city("Bangalore"+2).build());

                   return Person.builder()
                           .firstName("Rajat"+count)
                           .lastName("Sharma"+count)
                           .age(ThreadLocalRandom.current().nextInt(21, 38))
                           .hobbies(hobbyList)
                           .addresses(addressList)
                           .build();
               })
               .toList();
    }

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    //  Alternatively we can also enable Mockito Annotations Programmatically by invoking MockitoAnnotations.openMocks()
    // 'AutoCloseable' used without 'try'-with-resources statement
    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    // Automatically close mocks after each test
    @AfterEach
    public void tearDown(){
        // No need to manually close mocks when using try-with-resources
    }

    // Initialize mocks before each test
    @BeforeEach
    public void setUp() throws Exception {
        // Use try-with-resources to automatically close mocks
        try(var mockitoAnnotations=MockitoAnnotations.openMocks(this)){
            // Mock setUp
            when(personController.getByPersonAge(23,25)).thenReturn(personList);
        }
    }

    // Lastly we can also enable Mockito Annotations using MockitoJunit.rule() in this case remember to make our rule public
    @Rule
    public MockitoRule initRule= MockitoJUnit.rule();


    @Test
    void testGetByPersonAge(){
        List<Person> persons=personController.getByPersonAge(23,25);
        System.out.println(persons);
        assertEquals(personList, personController.getByPersonAge(23,25));
    }

}
