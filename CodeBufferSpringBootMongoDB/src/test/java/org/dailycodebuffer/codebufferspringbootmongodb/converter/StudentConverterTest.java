package org.dailycodebuffer.codebufferspringbootmongodb.converter;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class StudentConverterTest {

    @InjectMocks
    private static StudentConverter studentConverter;

    private static Student student;

    @BeforeAll
    public static void setUpStudentList(){
        System.out.println("setUpStudentList BeforeAll");

        student=Student.builder()
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

    }

    @Test
    void testconvertDtoToStudent_success(){
        StudentDTO studentDTO=new StudentDTO();

        studentDTO.setStudentId(student.getStudentId());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setLastName(student.getLastName());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setAge(student.getAge());
        studentDTO.setGender(student.getGender());
        studentDTO.setFavSubjects(student.getFavSubjects());
        studentDTO.setTotalSpendInBooks(student.getTotalSpendInBooks());
        studentDTO.setAddress(student.getAddress());
        studentDTO.setCreatedAt(student.getCreatedAt());
        studentDTO.setUpdatedAt(student.getUpdatedAt());

        Student student1 = studentConverter.convertDtoToStudent(studentDTO);

        assertEquals(studentDTO.getFirstName(), student1.getFirstName());
        assertNotNull(student1);
    }

    @Test
    void testconvertStudentToDTO_success(){

        StudentDTO studentDTO = studentConverter.convertStudentToDTO(student);

        assertEquals(student.getFirstName(), studentDTO.getFirstName());
        assertNotNull(studentDTO);
    }
}
