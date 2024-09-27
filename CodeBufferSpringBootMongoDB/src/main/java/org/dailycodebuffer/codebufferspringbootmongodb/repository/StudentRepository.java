package org.dailycodebuffer.codebufferspringbootmongodb.repository;

/*
    QuerydslPredicateExecutor is an interface in the Spring Data framework that allows the execution of
    QueryDsl Predicate instances. It provides several methods such as count, exists, findAll, findOne, and findBy
    that can be used to execute queries on a data store
 */

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("student")
public interface StudentRepository extends
        MongoRepository<Student,String> , QuerydslPredicateExecutor<Student> , QueryByExampleExecutor<Student> {

    // RAW Query using Mongo Performing CRUD


    List<Student> findByFirstNameAndAgeOrderByTotalSpendInBooksDesc(String firstname, Integer age);
    List<Student> findByFirstNameStartingWithAndAgeAfterOrderByTotalSpendInBooksDesc(String firstnameRegex, Integer age);

    /*@Query(value = "{ 'name' : ?0, 'age' : ?1 }", fields = "{ 'studentId' : 1 }")
    String saveStudentAndGetId(String name, int age);*/


    // 1  >>>>>> Generated Query Methods ::: GENERATED QUERY METHODS
    List<Student> findByFirstName(String firstName);
    List<Student> findByFirstNameStartingWith(String name);
    List<Student> findByFirstNameEndingWith(String nameRegex);

    List<Student> findByAgeBetween(Integer ageMin, Integer ageMax);

    // Like and OrderBy
    // looking for all students that have names containing the letter A,
    // and we’re also going to order the results by age, in descending order:
    List<Student> findByFirstNameLikeOrderByAgeDesc(String name);




    // 2  >>>>>> JSON QUERY METHODS :::::  JSON Query Methods
    // If we can’t represent a query with the help of a method name or criteria,
    // we can do something more low level, use the @Query annotation.
    // we can specify a raw query as a Mongo JSON query string.


        // FindBy
    //@Query(value = "{'firstName':?0, 'lastName': ?1}")
    @Query(value = "{'firstName': { $eq : ?0 } , 'lastName': ?1 }", fields = "{'address': 0}")
    List<Student> getStudentsByFirstNameAndLastName(String fname,String lname);

    // Regex using $regex
    @Query(value="{'firstName': {  $regex : ?0 }}")
    List<Student> getStudentByFirstNameStartingWith(String fname); // "^A"

    @Query(value="{'firstName': { $regex: ?0 }}")
    List<Student> getStudentByFirstNameEndWith(String fname); // "C$"

    // $lt and $gt
    //  the method has 2 parameters, we’re referencing each of these by index in the raw query, ?0 and ?1:
    // Sorting with @Query Annotation
    @Query(value="{'age': { $gte:  ?0, $lte:  ?1}}")
    List<Student> getStudentByAgeBetween(Integer minAge, Integer maxAge, Sort sort);

    // NESTED QUERIES::::::::::::::::
    @Query(value="{'address.address1': ?0, 'address.city': ?1, 'age': { $gt: ?2}}")
    List<Student> getStudentAddress1AndCityAndAgeGreaterThan(String address, String city, Integer age);

    // 3  >>>>>> QueryDSL Queries QUERY_DSL QUERIES
    // MongoRepository has good support for the QueryDSL project, so we can leverage that nice, type-safe API here as well.


}
