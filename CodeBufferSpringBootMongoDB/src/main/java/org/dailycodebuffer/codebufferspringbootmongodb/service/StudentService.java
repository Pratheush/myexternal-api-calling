package org.dailycodebuffer.codebufferspringbootmongodb.service;


import com.mongodb.client.result.DeleteResult;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.QStudent;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.converter.StudentConverter;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.dailycodebuffer.codebufferspringbootmongodb.exceptions.StudentNotFoundException;
import org.dailycodebuffer.codebufferspringbootmongodb.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.schema.JsonSchemaObject;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
@Profile("student")
@Transactional
public class StudentService {

    private static final Log LOG = LogFactory.getLog(StudentService.class);

    private final StudentRepository studentRepository;

    private final MongoTemplate mongoTemplate;

    private final StudentConverter studentConverter;

    private final String FIRSTNAME="firstName";


    @Autowired
    public StudentService(StudentRepository studentRepository, MongoTemplate mongoTemplate,StudentConverter studentConverter) {
        this.studentRepository = studentRepository;
        this.mongoTemplate = mongoTemplate;
        this.studentConverter=studentConverter;
    }

    // GENERATED QUERY METHODS ----------------------------------------------------------------
    public List<StudentDTO> findByFirstNameStartingWithAndAgeAfterOrderByTotalSpendInBooksDesc(String firstName, Integer age){
        List<Student> studentList=studentRepository.findByFirstNameStartingWithAndAgeAfterOrderByTotalSpendInBooksDesc(firstName,age);
        List<StudentDTO> studentDTOList= new ArrayList<>();
        for(Student st : studentList) {
            StudentDTO studentDTO=studentConverter.convertStudentToDTO(st);
            studentDTOList.add(studentDTO);
        }
        return studentDTOList;
    }

    public List<Student> findByFirstNameStartingWith(String firstName){
        return studentRepository.findByFirstNameStartingWith(firstName);
    }

    public List<Student> findByFirstNameEndingWith(String firstName){
        return studentRepository.findByFirstNameEndingWith(firstName);
    }

    public List<Student> findByAgeBetween(Integer ageMin, Integer ageMax){
        return studentRepository.findByAgeBetween(ageMin,ageMax);
    }

    public List<Student> findByFirstNameLikeOrderByAgeDesc(String name){
        return studentRepository.findByFirstNameLikeOrderByAgeDesc(name);
    }

// ==============================================================================================================================

    /**
     *
     * APPLYING AND CONDITIONS ON CRITERIA
     * here when we are doing query.addCriteria(Criteria.where("firstName").is(fName)); and then in next statement
     * query.addCriteria(Criteria.where("lastName").is(lName)); then we have two criteria with AND in query
     * @param fName
     * @param lName
     * @return Student
     */
     public List<Student> findStudentByFirstNameAndLastNameCriteriaQuery(String fName,String lName){
         LOG.info("findStudentByFirstNameAndLastNameCriteriaQuery firstName: "+fName+" lastName: "+lName);
         Query query = new Query();

         // ONE WAY OF WRITING APPLYING AND CONDITIONS ON CRITERIA
         query.addCriteria(Criteria.where("firstName").is(fName));
         query.addCriteria(Criteria.where("lastName").is(lName));

         // ANOTHER WAY OF WRITING APPLYING AND CONDITIONS ON CRITERIA
        /* Criteria criteria = new Criteria();
         query.addCriteria(criteria.andOperator(
                 Criteria.where("firstName").is(fName),
                 Criteria.where("lastName").is(lName))
         );*/


         List<Student> students = mongoTemplate.find(query, Student.class);
         LOG.info("findStudentByFirstNameAndLastNameCriteria StudentsList :"+ students);
         return students;
     }

    /**
     * APPLYING OR CONDITIONS ON CRITERIA
     * here when we are doing query.addCriteria(Criteria.where("firstName").is(fName)); and then in next statement
     * query.addCriteria(Criteria.where("lastName").is(lName)); then we have two criteria with AND in query
     * @param fName
     * @param lName
     * @return Student
     */
    public List<Student> findStudentByFirstNameOrLastNameCriteriaQuery(String fName,String lName){
        LOG.info("findStudentByFirstNameOrLastNameCriteriaQuery firstName: "+fName+" lastName: "+lName);
        Query query = new Query();
        query.addCriteria(Criteria.where("firstName").is(fName));
        Criteria criteria = new Criteria();

        query.addCriteria(criteria.orOperator(
                Criteria.where("firstName").is(fName),
                Criteria.where("lastName").is(lName)
                )
        );

        List<Student> students = mongoTemplate.find(query, Student.class);
        LOG.info("findStudentByFirstNameOrLastNameCriteriaQuery StudentsList :"+ students);
        return students;
    }

    public List<Student> findStudentByFirstNameAndEmailCriteriaQuery(String fName,String email){
        LOG.info("findStudentByFirstNameOrLastNameCriteriaQuery firstName: "+fName+" email: "+email);
        Query query = new Query();

        query.addCriteria(Criteria.where("firstName").is(fName));
        // regex checking valid email
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$\n"));
        // criteria checking email exists or not
        query.addCriteria(Criteria.where("email").exists(true));
        // criteria if email is not null and not empty
        query.addCriteria(Criteria.where("email").ne(null).ne(""));
        // criteria if email not in the given array
        query.addCriteria(Criteria.where("email").nin("sonakshi@gmail.com","meena@gmail.com"));
        // criteria if email is in the given array
        query.addCriteria(Criteria.where("email").in("sonakshi@gmail.com","meena@gmail.com"));
        // criteria if gender is String type :: well this criteria doesn't make any sense but its just for learning. this way we can put criteria on type of field
        query.addCriteria(Criteria.where("gender").type(JsonSchemaObject.Type.STRING));
        // criteria if favorite subjects is String type :: well this criteria doesn't make any sense but its just for learning. this way we can put criteria on type of field
        query.addCriteria(Criteria.where("favSubjects").type(JsonSchemaObject.Type.stringType()));


        // another way of writing criteria and adding criteria to the query using AND or OR
        /*Criteria criteria = new Criteria();
        query.addCriteria(criteria.andOperator(
                Criteria.where("firstName").is(fName),
                Criteria.where("email").exists(true),
                Criteria.where("email").ne(null).ne("")
                )
        );*/

        List<Student> students = mongoTemplate.find(query, Student.class);
        LOG.info("findStudentByFirstNameOrLastNameCriteriaQuery StudentsList :"+ students);
        return students;
    }


    // Using Criteria Query with MongoTemplate
    // usage of  Is for creating query
    //  In the following example, we’ll look for users named Eric
    public List<Student> getStudentFnameByIS(String firstNameRegex){
        LOG.info("getStudentFnameByIS firstName: " + firstNameRegex);
        Query query = new Query();

        query.addCriteria(Criteria.where("firstName").regex("^"+firstNameRegex+".*\\d{2}$"));
        //query.addCriteria(Criteria.where(FIRSTNAME).is(firstNameRegex));
        List<Student> students=mongoTemplate.find(query,Student.class);
        LOG.info("Students Size: "+students.size());
        return students;
    }

    // using Regex works similarly to startingWith and endingWith operations.
    // In this example, we’ll look for all users that have names starting with A.
    public List<Student> getStudentFirstNameStartingWith(String firstName){
        Query query = new Query();
        query.addCriteria(Criteria.where(FIRSTNAME).regex("^"+firstName,"i"));
        List<Student> students=mongoTemplate.find(query,Student.class);
        LOG.info("getStudentFirstNameStartingWith StudentsList :"+ students);
        return students;
    }

    // In this example, we’ll look for all users that have names ending with c.
    public List<Student> getStudentsFirstNamesEndingWith(String firstName){
        Query query=new Query();
        query.addCriteria(Criteria.where(FIRSTNAME).regex(firstName+"$","i"));
        List<Student> students=mongoTemplate.find(query,Student.class);
        LOG.info("getStudentsFirstNamesEndingWith StudentsList :"+ students);
        return students;
    }

    // lt and gt Operator
    // create a criterion using the $lt (less than) and $gt (greater than) operators.
    // looking for all users between the ages of 20 and 50.
    public List<Student> getStudentAgeBetween(Integer ageMin,Integer ageMax){
        Query query=new Query();
        query.addCriteria(Criteria.where("age").gt(ageMin).lt(ageMax));
        List<Student> students=mongoTemplate.find(query,Student.class);
        LOG.info("getStudentAgeBetween StudentsList :"+ students);
        return students;
    }

    // Sort is used to specify a sort order for the results.
    // In this example, we’ll sort the results by their age.
    public List<StudentDTO> getStudentAgeSorted(){
        /*Query query=new Query();
        query.with(Sort.by(Sort.Direction.ASC,"age"));
        List<Student> students=mongoTemplate.find(query,Student.class);
        return students;*/

        //Query query=new Query();
        SortOperation sortOperation= Aggregation.sort(Sort.Direction.DESC,"age");

        Aggregation aggregation=Aggregation.newAggregation(sortOperation);
        AggregationResults<Student> aggregationResults=mongoTemplate.aggregate(aggregation,Student.class,Student.class);
        List<Student> students=aggregationResults.getMappedResults();
        List<StudentDTO> studentsDTOs=new ArrayList<>();
        for(Student std: students){
            StudentDTO stdDTO=studentConverter.convertStudentToDTO(std);
            studentsDTOs.add(stdDTO);
        }
        LOG.info("getStudentAgeSorted StudentsDTOList :"+ studentsDTOs);
        return studentsDTOs;

    }

    // Pageable Using MongoTemplate
    // MongoTemplate does not have methods to return Page.
    // Page can be used in conjunction with MongoDB repositories which is a specialized case of Spring data repositories.
    // Pageable
     public List<StudentDTO> searchStudentPageable(Integer page,Integer size){
        final Pageable pageable= PageRequest.of(page,size,Sort.Direction.DESC, "age");
        Query query=new Query();
                 query.with(pageable)
                         .addCriteria(Criteria.where("gender").ne(Gender.FEMALE));
         Page<Student> personPage=PageableExecutionUtils.getPage(
                 mongoTemplate.find(query,Student.class),
                 pageable,
                 ()-> mongoTemplate.count(query.skip(0).limit(0),Student.class)
         );
         List<Student> studentsList=personPage.getContent();
         List<StudentDTO> studentDTOS=new ArrayList<>();
         for(Student st:studentsList) studentDTOS.add(studentConverter.convertStudentToDTO(st));
         LOG.info("searchStudentPageable StudentsDTOList :"+ studentDTOS);
         return studentDTOS;
     }

// =============================================================================================================================


    // 3  >>>>>> QueryDSL Queries QUERY_DSL QUERIES
    // Using QueryDSL
    // MongoRepository has good support for the QueryDSL project, which provides a means to perform type-safe queries in Java.
    // QStudent is a class that is generated (via the Java annotation post-processing tool) which is a Predicate
    // that allows you to write type-safe queries. Notice that there are no strings in the query other than the
    // query parameters that we supply.

    //  Eq
    public List<Student> getStudentListByFirstName(String firstName){
        //QStudent qStudent= QStudent.student;
        QStudent qStudent1= new QStudent("student");
        Predicate predicate=qStudent1.firstName.equalsIgnoreCase(firstName);
        List<Student> studentList= (List<Student>) studentRepository.findAll(predicate);
        LOG.info("getStudentListByFirstName StudentList :"+ studentList);
        return studentList;
    }

    // QueryDSL Queries :: startingWith
    public List<Student> getStudentStartingWith(String firstName){
        //QStudent qStudent=new QStudent("student");
        QStudent qStudent= QStudent.student;
        Predicate predicate=qStudent.firstName.startsWithIgnoreCase(firstName);
        Iterable<Student> stuIterable=studentRepository.findAll(predicate);
        return StreamSupport.stream(stuIterable.spliterator(),false).toList();
    }

    // QueryDSL Queries :: endingWith
    public List<Student> getStudentEndingWith(String firstName){
        QStudent qStudent=new QStudent("student");
        Predicate predicate=qStudent.firstName.endsWithIgnoreCase(firstName);
        List<Student> students=new ArrayList<>();
        Iterable<Student> stuIterable=studentRepository.findAll(predicate);
        stuIterable.forEach(students::add);
        return students;
    }

    // Between
    //The next query will return users with ages between 20 and 50, similar to the previous sections
    public List<Student> getStudentsBetweenAge(Integer minAge,Integer maxAge){
        QStudent qStudent=new QStudent("student");
        Predicate predicate=qStudent.age.between(minAge,maxAge);
        List<Student> students=new ArrayList<>();
        Iterable<Student> stuIterableIter=studentRepository.findAll(predicate);
        stuIterableIter.forEach(students::add);
        return students;
    }

    /*
    .* matches any character (except for a newline) zero or more times.
     2 matches the digit 2.
    .* matches any character (except for a newline) zero or more times.

    OrderSpecifier and Predicate are two different types of objects in Querydsl.
    OrderSpecifier is used to specify the order in which the results should be returned,
    while Predicate is used to filter the results based on certain criteria.

    here matchName containing firstName has 2 in it
     */
    public List<StudentDTO> getAllStudentByQueryDSLByMatchingNameGenderTotalSpendInBooksOrder(String matchFName,String matchLName){
        QStudent qStudent = new QStudent("student");
        OrderSpecifier<BigDecimal> spendingOrder=qStudent.totalSpendInBooks.desc();
        Predicate fNameMatchPredicate=qStudent.firstName.matches(".*"+matchFName+".*")
                                        .andAnyOf(
                                                qStudent.lastName.containsIgnoreCase(matchLName))
                                                .and(qStudent.gender.eq(Gender.FEMALE));
        Iterable<Student> studentIterable=studentRepository.findAll(fNameMatchPredicate, spendingOrder);
        List<Student> stuList= StreamSupport.stream(studentIterable.spliterator(),false).toList();
        List<StudentDTO> stuDtos=new ArrayList<>();
        for(Student std: stuList) stuDtos.add(studentConverter.convertStudentToDTO(std));
        return stuDtos;
    }

    public List<Student> getAllStudent(Integer pageNumber,Integer pageSize,String name) {
        Pageable pageable=PageRequest.of(pageNumber,pageSize,Sort.Direction.DESC,"age");

        PageRequest firstPageRequest = PageRequest.of(0, 3); //0 is the page index whereas 3 is the size of each page
        PageRequest secondPageRequest = PageRequest.of(0, 3, Sort.by("age")); //returns sorted result by salary in ascending order
        PageRequest thirdPageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "age"); //returns sorted result by salary in descending order

        LOG.info(studentRepository.findAll(firstPageRequest).get().toString());
        LOG.info(studentRepository.findAll(secondPageRequest).getContent().toString());
        LOG.info(studentRepository.findAll(thirdPageRequest).getContent().toString());

        QStudent qStudent=QStudent.student;
        Predicate predicate=qStudent.firstName.containsIgnoreCase(name);

        return studentRepository.findAll(predicate,pageable).getContent();
    }




// ==============================================================================================================================




    //  We can use findAll(Example empExample) method by providing an example object to the method.
    //  An Example takes a data object and a specification how to match properties. Below is an example:
    /*
        Limitations
                Like all things, the Query by Example API has some limitations:

                1. Nesting and grouping statements aren’t supported. For example:
                            (firstName = ?0 and lastName = ?1) or seatNumber = ?2
                2. String matching only includes exact, case-insensitive, starts, ends, contains, and regex
                3. All types other than String are exact-match only

           Applying and/or conjunction on non-null properties
             (1). 'and' conjunction' methods
                     matching()/matchingAll()
             (2). 'or' conjunction method
                     matchingAny();

Above methods are static factory methods, so they are starting point. The return value of ExampleMatcher can be chained further as
all other methods also return ExampleMatcher.
     */
    public List<Student> queryByExampleFnameStartingWith(String matchExampleFname,BigDecimal spendInBooks){
        LOG.info("StudentService ::::  queryByExampleFnameStartingWith() >>>> matchExampleFname::: "+ matchExampleFname + " spendInBooks ::::: " + spendInBooks);
        /*
        Example employeeExample = Example.of(new Employee("Emp 1"));
        employeeRepository.findAll(employeeExample);
         */
        /* Example<Student> example = Example.of(Student.builder().firstName("Raj 1").build());
        return studentRepository.findAll(example);*/
        /*Address address= Address.builder()
                .address1("Kerala")
                .address2("Kochi")
                .city("Indirapuram")
                .build();
        String dateString = "2024-01-06T19:17:33.091";
        LocalDateTime dateTime = LocalDateTime.parse(dateString);*/
        Student student= Student.builder()
                .firstName(matchExampleFname)
                .totalSpendInBooks(spendInBooks)
               /* .lastName("Kent")
                .email("erickent@gmail.com")
                .gender(Gender.MALE)
                .age(27)
                .favSubjects(Arrays.asList("Coding", "Watching Kdramas", "Playing Chess"))
                .address(address)
                .createdAt(dateTime)
                .updatedAt(null)*/
                .build();

         /*
            Other String matching criteria
            For all paths:
            withStringMatcher(StringMatcher stringMatcher)
          */
        //it will return all Student with firstName containing 'aj' AND totalSpendInBooks is equal to 4707
        ExampleMatcher exampleMatcher=ExampleMatcher.matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("totalSpendInBooks",ExampleMatcher.GenericPropertyMatchers.exact());

        //it will return all Student with firstName containing 'aj' OR totalSpendInBooks is equal to 4707
        ExampleMatcher exampleMatcher1=ExampleMatcher.matchingAny()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("totalSpendInBooks",ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Student> studentExample=Example.of(student,exampleMatcher);

        LOG.info("Student::>>>>>>>"+ student);
        LOG.info("exampleMatcher::>>>>>>>"+ exampleMatcher);
        LOG.info("studentExample::>>>>>>>"+ studentExample);
        LOG.info("LIST STUDENT SIZE::>>>>>>>"+ studentRepository.findAll(studentExample).size());

        return studentRepository.findAll(studentExample);
    }

    // EXAMPLES Case-Sensitive Matching ----------------------------------------------------------------
    /*
            Case sensitivity
            withIgnoreCase(),
            withIgnoreCase(true/false),
            withIgnoreCase(String... propertyPaths)
     */
    public List<Student> exampleCaseSensitiveMatch(String firstName,String lastName){
        LOG.info("exampleCaseSensitiveMatch :::::: firstName: " + firstName+" lastName "+ lastName);
        Student student=new Student();
        student.setFirstName("^"+firstName);
        student.setLastName("^"+lastName);
        //student.setGender(Gender.FEMALE);
        //student.setTotalSpendInBooks(BigDecimal.valueOf(4652));
        //student.setAge(24);


        /*
                For specified path:
                withMatcher(String propertyPath, GenericPropertyMatchers genericPropertyMatchers)
                withMatcher(String propertyPath, MatcherConfigurer matcherConfigurer)
         */
        //Example<Student> stExWithMatcherGenericPropertyMatchersExact=Example.of(student,ExampleMatcher.matching()
        //                              .withMatcher("firstName",ExampleMatcher.GenericPropertyMatchers.exact()));



        // For Specified Path :::    withMatcher(String propertyPath, MatcherConfigurer matcherConfigurer)
        // Below withMatcher without Lambda
        ExampleMatcher exampleFnameLnameWithoutLambda=ExampleMatcher.matching()
                .withMatcher(FIRSTNAME, new ExampleMatcher.MatcherConfigurer< ExampleMatcher.GenericPropertyMatcher >(){
                    @Override
                    public void configureMatcher(ExampleMatcher.GenericPropertyMatcher matcher) {
                            matcher.regex();
                    }
                })
                .withMatcher("lastName", new ExampleMatcher.MatcherConfigurer< ExampleMatcher.GenericPropertyMatcher >(){
            @Override
            public void configureMatcher(ExampleMatcher.GenericPropertyMatcher matcher) {
                matcher.regex();
            }
        });

        // Below withMatcher with Lambda:::::::::::
        ExampleMatcher exampleFnameLnameWithLambda=ExampleMatcher.matching()
                .withMatcher(FIRSTNAME, matcher -> matcher.regex())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatcher::regex);

        Example<Student> exampleStudentWithMatcherRegex=Example.of(student,exampleFnameLnameWithLambda);



            //  For all paths:  USING >>>>>    withStringMatcher(StringMatcher stringMatcher)
        //ExampleMatcher exampleMatcher=ExampleMatcher.matching()
        //                               .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);

        //  ignoring case for all non-null properties
        ExampleMatcher exampleMatcher=ExampleMatcher.matchingAll().withIgnoreCase();
        //  ignoring case for just 'firstName' property not for lastName property
        ExampleMatcher exampleMatcherWithIgnoreCase4FirstName=ExampleMatcher.matchingAll().withIgnoreCase("firstName");

        Example<Student> studentExampleWithIgnoreCase=Example.of(student,exampleMatcher);

        List<Student> studOutput=studentRepository.findAll(exampleStudentWithMatcherRegex);

        LOG.info("Student OUTPUT::::"+ studOutput);
        return studOutput;
    }

    /*
        Ignoring paths
        withIgnorePaths(String... ignoredPaths)

        Example
        In following example, assuming Person entity's id is type of 'long' which will default to 0 instead of null.
        In that case we must ignore id property via ExampleMatcher:

         Person person = new Person();
         person.setName("Tara");
         ExampleMatcher exampleMatcher = ExampleMatcher.matching()//same as matchingAll()
                                                       .withIgnorePaths("id");
         Example<Person> personExample = Example.of(person, exampleMatcher);




        Handling nulls
        withIncludeNullValues()
        withIgnoreNullValues()
        withNullHandler(NullHandler nullHandler)
     */



    //  Transforming property values
    //withTransformer(String propertyPath, PropertyValueTransformer propertyValueTransformer)

    /**
     * The transformation logic is applied using withTransformer(), which transforms the firstName to uppercase before the query.
     * This is useful when you're matching a case-sensitive field.
     *
     * ExampleMatcher Settings:
     * .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.exact()): This ensures that firstName is matched exactly after being transformed to uppercase.
     * Depending on your matching strategy (whether it's exact, case-insensitive, etc.), you can tweak this matcher.
     *
     * Query Example Creation:
     * The exampleStudent object is created using both the probe (student) and the matcher (exampleTransformWithLambda).
     *
     * Here Transforming the Value rajni to RAJNI and find RAJNI from the Database.
     *
     * @param fname
     * @return
     */
    public List<Student> exampleTransformingMatch(String fname){
        LOG.info("exampleTransformingMatch >> fname :: "+fname);

        // Create a student probe object with the firstName as the query criteria
        Student student=new Student();
        student.setFirstName(fname);
        //student.setEmail("raj4@gmail.com");

        // Use ExampleMatcher with transformer to transform the firstName to uppercase
        ExampleMatcher exampleTransformWithoutLambda=ExampleMatcher.matching()
                .withTransformer(FIRSTNAME,new ExampleMatcher.PropertyValueTransformer(){
                    @Override
                    public Optional<Object> apply(Optional<Object> o) {
                        LOG.info(o.get());
                        LOG.info(Optional.of(((String) o.get()).toUpperCase()));
                        return Optional.of(((String) o.get()).toUpperCase());
                        /*if (o.isPresent()) {
                            return Optional.of(((String) o.get()).toUpperCase());
                        }
                        return o;*/
                    }
                });

        // use in postman tool :: http://localhost:8081/api/student/example/transform?name=rajni
        // Use ExampleMatcher with transformer to transform the firstName to uppercase
        ExampleMatcher exampleTransformWithLambda=ExampleMatcher.matchingAll()
                .withMatcher(FIRSTNAME, ExampleMatcher.GenericPropertyMatchers.exact()) // exact match
                .withTransformer(FIRSTNAME, o -> o.map(object -> object.toString().toUpperCase()));

        // ^Ra.*\\d{2}$: This pattern means the string starts with "Ra" (^Ra), can contain any characters (.*), and ends with exactly two digits (\\d{2}$).
        ExampleMatcher exampleTransformWithLambdaWithMatchingStartFname=ExampleMatcher.matchingAll()
                .withMatcher(FIRSTNAME, ExampleMatcher.GenericPropertyMatchers.regex()) // exact match
                .withTransformer(FIRSTNAME, o -> o.map(object ->object.toString().matches("^Ra.*\\d{2}$")));

        // Create an Example instance using the matcher
        Example<Student> exampleStudent = Example.of(student, exampleTransformWithLambda);

        // Query the repository with the Example object
        List<Student> students =studentRepository.findAll(exampleStudent);

        LOG.info("exampleTransformWithLambda ::"+ exampleTransformWithLambda);
        LOG.info("exampleStudent"+ exampleStudent.getProbe());
        LOG.info("Students list size::"+students.size());
        return students;
    }





// ==============================================================================================================================

    // JSON QUERY METHODS ------------------------------------------------------------------------------------------------
    public List<Student> getStudentByFirstNameAndLastName(String firstName, String lastName){
        return studentRepository.getStudentsByFirstNameAndLastName(firstName, lastName);
    }

    public List<Student> getStudentByFirstNameStartingWith(String firstName){
        return studentRepository.getStudentByFirstNameStartingWith("^"+firstName);
    }

    public List<Student> getStudentByFirstNameEndingWith(String firstName){
        return studentRepository.getStudentByFirstNameEndWith(firstName+"$");
    }

    public List<Student> getStudentByAgeBetween(Integer minAge,Integer maxAge){
        Sort sortAge=Sort.by(Sort.Direction.DESC,"age");
        return studentRepository.getStudentByAgeBetween(minAge,maxAge,sortAge);
    }

    public List<Student> getStudentAddress1AndCityAndAgeGreaterThan(String address1, String city, Integer age){
        return studentRepository.getStudentAddress1AndCityAndAgeGreaterThan(address1,city,age);
    }


// ==============================================================================================================================

    // CURD USING MongoTemplate================================================================

    // Using Criteria Query with MongoTemplate  ::::::::::: execute criteria queries using MongoTemplate.

    // SAVE OPERATION
    public StudentDTO saveStudent(StudentDTO studentdto){
        Student st=studentConverter.convertDtoToStudent(studentdto);
        st.setCreatedAt(java.time.LocalDateTime.now());
        //Student savedStudent=mongoTemplate.save(st);
        Student savedStudent=studentRepository.save(st);
        return studentConverter.convertStudentToDTO(savedStudent);
    }

    // UPDATE OPERATION
    // For update, we first fetch an existing document using Is operator in the Query and
    // then save the document after updating the different attributes.
    public StudentDTO updateStudent(StudentDTO studentDTO){

        Student student=studentConverter.convertDtoToStudent(studentDTO);

        Query query=new Query();
        query.addCriteria(Criteria.where("email").is(student.getEmail()));
        Update update=new Update();
        update.set(FIRSTNAME, student.getFirstName());
        update.set("lastName", student.getLastName());
        update.set("age", student.getAge());
        //update.set("email", student.getEmail());
        update.set("gender", student.getGender());
        update.set("address", student.getAddress());
        update.set("favSubjects", student.getFavSubjects());
        update.set("totalSpendInBooks", student.getTotalSpendInBooks());
        update.set("updatedAt", java.time.LocalDateTime.now());
        Student resultStudent= mongoTemplate.findAndModify(query,update,Student.class);

        if (resultStudent == null){
            throw new StudentNotFoundException("No Student Found");
        }
        else{
            return studentConverter.convertStudentToDTO(resultStudent);
        }

    }

    // DELETE OPERATION
    public DeleteResult deleteStudentByFirstName(String firstName){
        Query query=new Query();
        query.addCriteria(Criteria.where(FIRSTNAME).is(firstName));
        return mongoTemplate.remove(query,Student.class);
    }

    public void deleteStudentById(String studentId){
        Optional<Student> optStudent = studentRepository.findById(studentId);
        if (optStudent.isPresent()) {
            studentRepository.deleteById(studentId);
        }
        else {
            throw new StudentNotFoundException("No Student Found By given StudentId");
        }
    }

    // Projections with MongoTemplate
    // As we saw in the above examples in our MongoRepository implementation,
    // we used fields attribute to project only the columns which we are intrested in.
    // The same result can be achieved with MongoTemplate too and this is achieved with the include and exclude operators.
    // Below is an example: the address and createdAt and updatedAt will default to default value of null.
    public List<Student> getStudentByProjections(){
        Query query=new Query();
        query.fields().exclude("address").exclude("createdAt").exclude("updatedAt");
        query.addCriteria(Criteria.where(FIRSTNAME).in(List.of("Eric","Vasu")));
        return mongoTemplate.find(query,Student.class);
    }

    //Aggregation with Mongotemplate
    //
    //MongoDB provides multiple native operators to perform aggregation $group, $order, $sort, etc.
    // In spring data mongo, we can perform these operations with different aggregate functions.
    // In Spring Data, we perform these aggregation with mainly 3 classes
    // - Aggregation, AggregationOperation and AggregationResults.
    // MongoDB’s aggregation framework is modeled on the concept of data processing pipelines.
    // Documents enter a multi-stage pipeline that transforms the documents into an aggregated result.
    public List<Document> getStudentByAggregation(){

        //$match
        MatchOperation ageMatch=Aggregation.match(Criteria.where("age").gt(20));

        //$group
        GroupOperation groupByGender = Aggregation.group("gender")
                .avg("totalSpendInBooks").as("averageSpendInBooks");

        //$sort
        SortOperation sortAvg = Aggregation.sort(Sort.Direction.DESC, "averageSpendInBooks");

        ProjectionOperation projectionOperation= Aggregation.project()
                .andExpression("_id").as("GENDER")
                .andExpression("averageSpendInBooks").as("avgSpendingOnBooks")
                .andExclude("_id");

        Aggregation aggregation=Aggregation.newAggregation(ageMatch, groupByGender, sortAvg, projectionOperation);

        AggregationResults<Document> aggregationResults=mongoTemplate.aggregate(aggregation,Student.class, Document.class);

        return aggregationResults.getMappedResults();
    }
}
