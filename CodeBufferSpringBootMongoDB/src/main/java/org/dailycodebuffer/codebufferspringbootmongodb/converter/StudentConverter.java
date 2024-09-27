package org.dailycodebuffer.codebufferspringbootmongodb.converter;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class StudentConverter {

    public Student convertDtoToStudent(StudentDTO studentDTO){
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO,student);

        /*student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setAge(studentDTO.getAge());
        student.setGender(studentDTO.getGender());
        student.setFavSubjects(studentDTO.getFavSubjects());
        student.setTotalSpendInBooks(studentDTO.getTotalSpendInBooks());
        student.setAddress(studentDTO.getAddress());
        student.setCreatedAt(studentDTO.getCreatedAt());
        student.setUpdatedAt(studentDTO.getUpdatedAt());*/

        return student;
    }

    public StudentDTO convertStudentToDTO(Student student){

        StudentDTO studentDTO = new StudentDTO();
        BeanUtils.copyProperties(student,studentDTO);

        /*studentDTO.setStudentId(student.getStudentId());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setLastName(student.getLastName());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setAge(student.getAge());
        studentDTO.setGender(student.getGender());
        studentDTO.setFavSubjects(student.getFavSubjects());
        studentDTO.setTotalSpendInBooks(student.getTotalSpendInBooks());
        studentDTO.setAddress(student.getAddress());
        studentDTO.setCreatedAt(student.getCreatedAt());
        studentDTO.setUpdatedAt(student.getUpdatedAt());*/

        return studentDTO;
    }
}
