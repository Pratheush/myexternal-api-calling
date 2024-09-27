package org.dailycodebuffer.codebufferspringbootmongodb.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.dailycodebuffer.codebufferspringbootmongodb.exceptions.PersonNotFoundException;
import org.dailycodebuffer.codebufferspringbootmongodb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Profile("codebuffer")
@Transactional
@Slf4j
public class PersonServiceImpl implements PersonService{

    private final PersonRepository personRepository;


    private final MongoTemplate mongoTemplate;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, MongoTemplate mongoTemplate) {
        this.personRepository = personRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ObjectId save(Person person) { return personRepository.save(person).getPersonId(); }

    @Override
    public List<Person> getPersonStartWith(String name) {
        return personRepository.findByFirstNameStartsWith(name);
    }

    @Override
    public Boolean delete(ObjectId id) {
        AtomicBoolean deleted = new AtomicBoolean(false);
        personRepository.findById(String.valueOf(id)).ifPresentOrElse(person ->{
            personRepository.delete(person);
            deleted.set(true);
        },() ->{
            throw new PersonNotFoundException(String.format("Person Not Found with Id : %s",id));
        });
        return deleted.get();
    }

    @Override
    public List<Person> getByPersonAge(Integer minAge, Integer maxAge) {
        return personRepository.findPersonByAgeBetween(minAge,maxAge);
    }

    @Override
    public Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {

        log.info("PersonServiceImpl search called");
        log.info("PersonServiceImple >> name : {}, minAge : {}, maxAge : {}, city : {}, pageable : {}", name, minAge, maxAge,city,pageable);

        // Create the base query and include pagination
        Query query = new Query().with(pageable);

        // Create the criteria list
        List<Criteria> criteria = new ArrayList<>();

        // Add criteria for name (case-insensitive search)
        if(name !=null && !name.isEmpty()) {
            criteria.add(Criteria.where("firstName").regex(name,"i"));  // here the letter i is used as a flag to indicate case-insensitive matching
        }

        // Add criteria for age range
        if(minAge !=null && maxAge !=null) {
            criteria.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        // Add criteria for city
        if(city !=null && !city.isEmpty()) {
            criteria.add(Criteria.where("addresses.city").is(city));
        }

        // Add all criteria to the query using andOperator
        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria()
                    .andOperator(criteria.toArray(new Criteria[0])));
                    //.andOperator(criteria));  // this is to verify that .andOperator() takes Collection of criteria
        }

        // Apply sorting by 'age' in descending order
        query.with(Sort.by(Sort.Direction.DESC, "age"));

        // Execute the query and apply pagination
        Page<Person> people = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Person.class),
                pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0),Person.class));

        log.info("PersonServiceImpl searchPerson() >> people {}", people);

        // If no people are found, throw an exception
        if(people.isEmpty()) throw new PersonNotFoundException("Person List Not Found");
        return people;
    }

    @Override
    public List<Document> getOldestPersonByCity() {
        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC,"age");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .first(Aggregation.ROOT)
                .as("oldestPerson");

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation,sortOperation,groupOperation);

        List<Document> person
                = mongoTemplate
                .aggregate(aggregation, Person.class,Document.class)
                .getMappedResults();
        return person;
    }

    @Override
    public List<Document> getPopulationByCity() {

        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .count().as("popCount");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC, "popCount");

        ProjectionOperation projectionOperation
                = Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("popCount").as("count")
                .andExclude("_id");

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation,groupOperation,sortOperation,projectionOperation);

        List<Document> documents
                = mongoTemplate.aggregate(aggregation,Person.class,Document.class)
                .getMappedResults();
        return  documents;
    }

    @Override
    public Person updatePerson(Person person,ObjectId personId) {
        log.info("PersonServiceImpl updatePerson called person :: {}", person);
        log.info("updatePerson personId : {}",personId);
        Person personFrmDb = mongoTemplate.findById(personId, Person.class);
        log.info("updatePerson person from Db : {}", personFrmDb);

        // to simulate NotFoundException
        //personFrmDb=null;

        if(personFrmDb!=null){
            personFrmDb.setFirstName(person.getFirstName());
            personFrmDb.setLastName(person.getLastName());
            personFrmDb.setAge(person.getAge());
            personFrmDb.setHobbies(person.getHobbies());
            personFrmDb.setAddresses(person.getAddresses());
        }else{
            log.error("PersonServiceImpl updatePerson PersonNotFoundException thrown");
            throw new PersonNotFoundException("Person Not Found");
        }
        return mongoTemplate.save(personFrmDb);
    }

    @Override
    public Person getPersonByNameAndAgePathVariable(String firstName, Integer age) {
        return personRepository.findPersonByFirstNameAndAge(firstName, age);
    }

    @Override
    public Person getPersonById(ObjectId objectIdPersonId) {
        return personRepository.findById(String.valueOf(objectIdPersonId)).orElseThrow(()-> new PersonNotFoundException("Person Not Found"));
    }

    @Override
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Override
    public Person createPersonOnStatus(Person person) {
        return personRepository.save(person);
    }
}
