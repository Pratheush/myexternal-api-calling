package org.dailycodebuffer.codebufferspringbootmongodb.dto;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDTO {

    private String studentId;
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
    private Gender gender;
    private Address address;
    private List<String> favSubjects;
    private BigDecimal totalSpendInBooks;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
