package com.mylearning.journalapp.clientresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)          //  It is commonly used in Spring Boot applications to exclude properties with empty/null/default values from serialization
public class Person {

    // earlier it was String
    @JsonProperty(value="personId")
    private ObjectId personId;

    private String firstName;

    private String lastName;

    private Integer age;

    private List<String> hobbies;

    private List<Address> addresses;
}
