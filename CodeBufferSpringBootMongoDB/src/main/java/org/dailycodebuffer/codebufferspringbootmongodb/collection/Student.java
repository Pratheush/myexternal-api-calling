package org.dailycodebuffer.codebufferspringbootmongodb.collection;

import com.querydsl.core.annotations.QueryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@QueryEntity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="student")
@Builder
@Profile("student")
public class Student {

    @Id
    private String studentId;
    private String firstName;
    private String lastName;
    private Integer age;
    @Indexed(unique = true)
    private String email;
    private Gender gender;
    private Address address;
    private List<String> favSubjects;
    private BigDecimal totalSpendInBooks;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
