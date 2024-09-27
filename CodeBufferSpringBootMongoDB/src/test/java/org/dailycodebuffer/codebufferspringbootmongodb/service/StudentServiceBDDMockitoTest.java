package org.dailycodebuffer.codebufferspringbootmongodb.service;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.converter.StudentConverter;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.dailycodebuffer.codebufferspringbootmongodb.exceptions.StudentNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willReturn;

@ExtendWith(MockitoExtension.class)
class StudentServiceBDDMockitoTest {


    @Mock
    private StudentConverter studentConverter;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName(" test Update Student WIthout Exception THrown")
    void testUpdateStudent_Success(){

        // Mock converted student
        Student convertedStudent = new Student();
        convertedStudent.setLastName("Ravi");
        convertedStudent.setLastName("Sharma");
        convertedStudent.setAge(25);

        // // Mock input data
        StudentDTO inputStudentDTO = new StudentDTO();
        inputStudentDTO.setFirstName("Ravi");
        inputStudentDTO.setLastName("Sharma");
        inputStudentDTO.setAge(25);

        // Mock expected Student
        Student expectedResult = new Student(); // Set your expected result here
        expectedResult.setFirstName("Ravi");
        expectedResult.setLastName("Sharma");
        expectedResult.setAge(25);

        // Mock expected DTO
        StudentDTO expectedDTO = new StudentDTO(); // Set your expected DTO here
        expectedDTO.setFirstName("Ravi");
        expectedDTO.setLastName("Sharma");
        expectedDTO.setAge(25);

        //  TRADITIONAL MOCKING :: Stubbing
        // Mock the behavior of StudentConverter  DTO to Student
        //Mockito.when(studentConverter.convertDtoToStudent(inputStudentDTO)).thenReturn(convertedStudent);
        // Mock the behavior of MongoTemplate
        //Mockito.when(mongoTemplate.findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class))).thenReturn(expectedResult);
        // Mock the behavior of StudentConverter Student to DTO
        //Mockito.when(studentConverter.convertStudentToDTO(expectedResult)).thenReturn(expectedDTO);

        // //  BDDMockito MOCKING :: Stubbing
        BDDMockito.given(studentConverter.convertDtoToStudent(inputStudentDTO)).willReturn(convertedStudent);
        BDDMockito.given(mongoTemplate.findAndModify(BDDMockito.any(Query.class),BDDMockito.any(Update.class),BDDMockito.eq(Student.class))).willReturn(expectedResult);
        BDDMockito.given(studentConverter.convertStudentToDTO(expectedResult)).willReturn(expectedDTO);

        // Act :: Call the method to be tested
        StudentDTO resultDTO = studentService.updateStudent(inputStudentDTO);

        // Assert
        assertEquals(inputStudentDTO.getFirstName(), resultDTO.getFirstName());
        assertNotNull(resultDTO);
        assertEquals(inputStudentDTO.getLastName(),resultDTO.getLastName());

        // Verify interactions
        /*Mockito.verify(studentConverter,Mockito.times(1)).convertDtoToStudent(inputStudentDTO);
        Mockito.verify(mongoTemplate,Mockito.times(1)).findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class));
        Mockito.verify(studentConverter,Mockito.times(1)).convertStudentToDTO(expectedResult);*/

        BDDMockito.then(studentConverter).should().convertDtoToStudent(inputStudentDTO);
        BDDMockito.then(mongoTemplate).should(BDDMockito.times(1)).findAndModify(BDDMockito.any(Query.class),BDDMockito.any(Update.class),BDDMockito.eq(Student.class));
        BDDMockito.then(studentConverter).should(BDDMockito.times(1)).convertStudentToDTO(expectedResult);

    }


    @Test
    @DisplayName(" test Update Student WIth Exception THrown")
    void testUpdateStudent_Failure(){

        // Mock converted student
        Student convertedStudent = new Student();
        convertedStudent.setFirstName("Ravi");
        convertedStudent.setLastName("Sharma");
        convertedStudent.setAge(25);

        // // Mock input data
        StudentDTO inputStudentDTO = new StudentDTO();
        inputStudentDTO.setFirstName("Ravi");
        inputStudentDTO.setLastName("Sharma");
        inputStudentDTO.setAge(25);


        // Mock the behavior of StudentConverter  DTO to Student
        //Mockito.when(studentConverter.convertDtoToStudent(inputStudentDTO)).thenReturn(convertedStudent);
        // Mock the behavior of MongoTemplate
        //Mockito.when(mongoTemplate.findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class))).thenReturn(null);

        BDDMockito.given(studentConverter.convertDtoToStudent(inputStudentDTO)).willReturn(convertedStudent);
        BDDMockito.willThrow(new StudentNotFoundException("No Student Found")).given(mongoTemplate).findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class));

        // Verify interactions
        /*Mockito.verify(studentConverter,Mockito.times(2)).convertDtoToStudent(inputStudentDTO);
        Mockito.verify(mongoTemplate,Mockito.times(2)).findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class));
        Mockito.verify(studentConverter, Mockito.never()).convertStudentToDTO(Mockito.any(Student.class));*/


        try{
            studentService.updateStudent(inputStudentDTO);
            fail("StudentNotFoundException Thrown",new StudentNotFoundException("No Student Found"));
        }catch (StudentNotFoundException e){
            assertThat(e, CoreMatchers.instanceOf(StudentNotFoundException.class));
            assertThat(e.getMessage(),CoreMatchers.containsStringIgnoringCase("No Student Found"));
        }

        BDDMockito.then(studentConverter).should(BDDMockito.atLeast(1)).convertDtoToStudent(inputStudentDTO);
        BDDMockito.then(mongoTemplate).should(BDDMockito.atLeast(1)).findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class));
        //BDDMockito.then(studentConverter).should(BDDMockito.never()).convertStudentToDTO(BDDMockito.any(Student.class));
        BDDMockito.then(studentConverter).shouldHaveNoMoreInteractions(); // both verify statements are okey for studentConverter with never() and shouldHaveNoMoreInteractions()
    }

    @Test
    @DisplayName(" test Update Student WIth Dynamic Value Null")
    void testUpdateStudent_DYNAMIC_VALUE_Null() {

        // Mock converted student
        Student convertedStudent = new Student();
        convertedStudent.setFirstName("Ravi");
        convertedStudent.setLastName("Sharma");
        convertedStudent.setAge(25);

        // // Mock input data
        StudentDTO inputStudentDTO = new StudentDTO();
        inputStudentDTO.setFirstName("Ravi");
        inputStudentDTO.setLastName("Sharma");
        inputStudentDTO.setAge(25);


        // Mock the behavior of StudentConverter  DTO to Student
        //Mockito.when(studentConverter.convertDtoToStudent(inputStudentDTO)).thenReturn(convertedStudent);
        // Mock the behavior of MongoTemplate
        //Mockito.when(mongoTemplate.findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class))).thenReturn(null);

        BDDMockito.given(studentConverter.convertDtoToStudent(inputStudentDTO)).willReturn(convertedStudent);
        BDDMockito.given(mongoTemplate.findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class)))
                .will((InvocationOnMock invocationOnMock) -> {
                    Object[] args=invocationOnMock.getArguments();
                   /* Student st=(Student) args[2];
                    Query q=(Query) args[0];
                    Update update=(Update) args[1];
                    assertThat(st,CoreMatchers.instanceOf(Student.class));
                    assertThat(q,CoreMatchers.instanceOf(Query.class));
                    assertThat(update,CoreMatchers.instanceOf(Update.class));*/
                    //System.out.println(Arrays.toString(args));

                    return null;
                });

        try{
            studentService.updateStudent(inputStudentDTO);
            //fail("StudentNotFoundException Thrown",new StudentNotFoundException("No Student Found"));
        }catch (StudentNotFoundException e){
            assertThat(e, CoreMatchers.instanceOf(StudentNotFoundException.class));
            assertThat(e.getMessage(),CoreMatchers.containsStringIgnoringCase("No Student Found"));
        }

        BDDMockito.then(studentConverter).should().convertDtoToStudent(inputStudentDTO);
        BDDMockito.then(mongoTemplate).should().findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class));
        BDDMockito.then(studentConverter).shouldHaveNoMoreInteractions();


    }

}
