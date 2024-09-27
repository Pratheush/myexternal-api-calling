package org.dailycodebuffer.codebufferspringbootmongodb.controller;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.converter.StudentConverter;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.dailycodebuffer.codebufferspringbootmongodb.service.StudentService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @InjectMocks        // Mockito is going to create Proxy Object of StudentController and inject into StudentControllerTest
    private StudentController studentController;

    @Mock       // Mockito is going to give memory to StudentService and Mockito will inject this dummy/proxy StudentService object into proxy/dummy object of StudentController
    private StudentService studentService;

    /*@Autowired
    private static StudentConverter studentConverter;*/

    private static List<StudentDTO> studentDTOS=new ArrayList<StudentDTO>();

    @BeforeAll
    public static void setUpStudentList(){
        System.out.println("setUpStudentList BeforeAll");

        StudentConverter studentConverter1=new StudentConverter();

        Student s1=Student.builder()
                .studentId("657ae2488a36df4ec5c2efc0")
                .firstName("Rajat")
                .lastName("Kumar")
                .age(23)
                .gender(Gender.MALE)
                .email("rajatkumar@gmail.com")
                .favSubjects(Arrays.asList("Coding", "Chess"))
                .totalSpendInBooks(BigDecimal.valueOf(3214))
                .address(new Address("Karnataka","Marathahalli", "Bangalore"))
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        Student s2=Student.builder()
                .studentId("657ae2488a36df4ec5c2efb9")
                .firstName("Rajat")
                .lastName("Sharma")
                .age(23)
                .gender(Gender.MALE)
                .email("rajatsharma@gmail.com")
                .favSubjects(Arrays.asList("Coding", "Chess"))
                .totalSpendInBooks(BigDecimal.valueOf(2214))
                .address(new Address("Karnataka","Marathahalli", "Bangalore"))
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        studentDTOS.add(studentConverter1.convertStudentToDTO(s1));
        studentDTOS.add(studentConverter1.convertStudentToDTO(s2));
    }

    @AfterAll
    public static void destroyStudentList(){
        System.out.println("setUpStudentList AfterAll");
        studentDTOS.clear();
    }

    // given when().thenReturn() test driven development approach.
    @Test
    @DisplayName("Test Success Scenario find by firstname Raj and 22 and OrderBy TotalSpendInBooksDesc")
    void testFindByFirstNameAndAgeOrderByTotalSpendInBooksDesc(){

        //  Do not make actual call studentService.findByFirstNameAndAgeOrderByTotalSpendInBooksDesc() inside StudentController rather return dummy object
        Mockito.when(studentService.findByFirstNameAndAgeOrderByTotalSpendInBooksDesc(Mockito.anyString(),Mockito.anyInt())).thenReturn(studentDTOS);

        ResponseEntity<List<StudentDTO>> responseStudentList = studentController.findByFirstNameAndAgeOrderByTotalSpendInBooksDesc("Rajat", 23);
        assertNotNull(responseStudentList.getBody());
        assertEquals(HttpStatus.OK, responseStudentList.getStatusCode());
        assertEquals(HttpStatus.OK.value(), responseStudentList.getStatusCodeValue());
    }

    @Test
    @DisplayName("test success scenario saving student")
    void testSaveStudent(){

        Mockito.when(studentService.saveStudent(Mockito.any(StudentDTO.class))).thenReturn(studentDTOS.getFirst());

        ResponseEntity<StudentDTO> response = studentController.saveStudent(studentDTOS.getFirst());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
    }

    @Test
    @DisplayName("test for update student")
    void testUpdateStudent(){

        Mockito.when(studentService.updateStudent(Mockito.any(StudentDTO.class))).thenReturn(studentDTOS.getLast());

        ResponseEntity<StudentDTO> response=studentController.updateStudent(studentDTOS.get(1));
        assertNotNull(response.getBody());
        assertEquals("Rajat",response.getBody().getFirstName());
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }
}
