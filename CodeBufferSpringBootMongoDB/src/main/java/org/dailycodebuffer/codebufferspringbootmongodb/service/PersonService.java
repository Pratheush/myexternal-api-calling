package org.dailycodebuffer.codebufferspringbootmongodb.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface PersonService {
    ObjectId save(Person person);

    List<Person> getPersonStartWith(String name);

    Boolean delete(ObjectId id);

    List<Person> getByPersonAge(Integer minAge, Integer maxAge);

    Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable);

    List<Document> getOldestPersonByCity();

    List<Document> getPopulationByCity();

    Person updatePerson(Person person, ObjectId personId);

    Person getPersonByNameAndAgePathVariable(String firstName, Integer age);

    Person getPersonById(ObjectId objectIdPersonId);

    List<Person> getAllPersons();

    Person createPersonOnStatus(Person person);

}
