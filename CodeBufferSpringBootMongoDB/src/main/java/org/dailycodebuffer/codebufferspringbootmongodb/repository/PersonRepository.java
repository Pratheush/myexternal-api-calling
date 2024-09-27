package org.dailycodebuffer.codebufferspringbootmongodb.repository;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("codebuffer")
public interface PersonRepository extends MongoRepository<Person,String> {

    List<Person> findByFirstNameStartsWith(String name);

    Person findPersonByFirstNameAndAge(String firstName, Integer age);

    //List<Person> findByAgeBetween(Integer min, Integer max);

    // using fields and mentioning the exact addresses variable of Person class with 0 means filtering out addresses
    // from the output response and if 1 is mentioned then addresses will be in the response
    @Query(value = "{ 'age' : { $gt : ?0, $lt : ?1}}",
            fields = "{addresses:  0}")
    List<Person> findPersonByAgeBetween(Integer min, Integer max);

    @Query("{ 'firstName': { $regex: ?0 }, 'age': { $gte: ?1, $lte: ?2 }, 'addresses.city': ?3 }")
    Page<Person> searchPersons(String name, Integer minAge, Integer maxAge, String city, Pageable pageable);
}
