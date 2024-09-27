package org.dailycodebuffer.codebufferspringbootmongodb.matcher;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.mockito.ArgumentMatcher;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.util.List;

public class StudentDTOMatcher implements ArgumentMatcher<StudentDTO> {

    private final String firstName;
    private final String lastName;
    private final Integer age;
    private final String email;
    private final Gender gender;
    private final Address address;
    private final List<String> favSubjects;
    private final BigDecimal totalSpendInBooks;
    private final java.time.LocalDateTime createdAt;
    private final java.time.LocalDateTime updatedAt;

    public StudentDTOMatcher(String firstName, String lastName, Integer age, String email, Gender gender, Address address, List<String> favSubjects, BigDecimal totalSpendInBooks, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.favSubjects = favSubjects;
        this.totalSpendInBooks = totalSpendInBooks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    @Override
    public boolean matches(StudentDTO studentDTO) {
        return studentDTO !=null &&
                firstName.equals(studentDTO.getFirstName()) &&
                lastName.equals(studentDTO.getLastName()) &&
                email.equals(studentDTO.getEmail()) &&
               gender.equals(studentDTO.getGender()) &&
               age.equals(studentDTO.getAge()) &&
               totalSpendInBooks.equals(studentDTO.getTotalSpendInBooks()) &&
               favSubjects.equals(studentDTO.getFavSubjects()) &&
               address.equals(studentDTO.getAddress()) &&
                studentDTO.getStudentId() != null &&
                studentDTO.getCreatedAt() != null;
    }

   /* private  StudentDTO left;

    @Override
    public boolean matches(StudentDTO right) {
        return left.getFirstName().equals(right.getFirstName()) &&
                left.getLastName().equals(right.getLastName()) &&
                left.getEmail().equals(right.getEmail()) &&
                left.getGender().equals(right.getGender()) &&
                left.getAge().equals(right.getAge()) &&
                left.getTotalSpendInBooks().equals(right.getTotalSpendInBooks()) &&
                left.getFavSubjects().equals(right.getFavSubjects()) &&
                left.getAddress().equals(right.getAddress()) &&
                right.getStudentId() != null &&
                right.getCreatedAt() != null;
    }*/


}
