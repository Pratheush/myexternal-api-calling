package org.dailycodebuffer.codebufferspringbootmongodb.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.bson.types.ObjectId;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.springframework.hateoas.RepresentationModel;


import java.util.List;


/**
 *
 *   The PersonResource class extends the Hateoas Representation Model and is required if we want to convert the Person
 *   Entity to a pagination format
 *
 *   // If we dont want the sequence generated id to be returned we can remove it from this object itself.
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResource extends RepresentationModel<PersonResource> {

    @JsonProperty("personId")
    private ObjectId personId;  // If we dont want the sequence generated id to be returned we can remove it from this object itself.
    private String firstName;
    private String lastName;
    private Integer age;
    private List<String> hobbies;
    private List<Address> addresses;

    public PersonResource(Person person) {
        this.personId = person.getPersonId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.age = person.getAge();
        this.hobbies = person.getHobbies();
        this.addresses = person.getAddresses();
    }
// Getters and setters

}
