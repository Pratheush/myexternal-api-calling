package org.dailycodebuffer.codebufferspringbootmongodb.controller;

import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.dailycodebuffer.codebufferspringbootmongodb.service.StudentService;
import org.dailycodebuffer.codebufferspringbootmongodb.work.Profiles;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@Profile("student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GENERATED QUERY METHODS ----------------------------------------------------------------


    @GetMapping("/gen/fname_age/{firstName}/{age}")
    public ResponseEntity<List<StudentDTO>> findByFirstNameStartingWithAndAgeAfterOrderByTotalSpendInBooksDesc(
            @PathVariable(value="firstName") String firstName,
            @PathVariable(value = "age") Integer age
    ){
        List<StudentDTO> studentDTOList= studentService.findByFirstNameStartingWithAndAgeAfterOrderByTotalSpendInBooksDesc(firstName,age);
        return new ResponseEntity<>(studentDTOList, HttpStatus.OK);
    }

    @GetMapping("/gen/fname_starting-with/{firstName}")
    public List<Student> findByFirstNameStartingWith(@PathVariable String firstName){
        return studentService.findByFirstNameStartingWith(firstName);
    }

    @GetMapping("/gen/fname_ending-with/{firstName}")
    public List<Student> findByFirstNameEndingWith(@PathVariable String firstName){
        return studentService.findByFirstNameEndingWith(firstName);
    }

    @GetMapping("/gen/age-between")
    public List<Student> findByAgeBetween(@RequestParam(value="agemin") Integer ageMin, @RequestParam("agemax") Integer ageMax){
        return studentService.findByAgeBetween(ageMin,ageMax);
    }

    @GetMapping("/gen/name-like-order-age/{name}")
    public List<Student> findByFirstNameLikeOrderByAgeDesc(@PathVariable String name){
        return studentService.findByFirstNameLikeOrderByAgeDesc(name);
    }


    // JSON QUERY METHODS ----------------------------------------------------------------


    @GetMapping("/jsn/fname_lname/{firstName}/{lastName}")
    public List<Student> getStudentByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName){
        return studentService.getStudentByFirstNameAndLastName(firstName,lastName);
    }

    @GetMapping("/jsn/fname-staringwith/{firstName}")
    public List<Student> getStudentByFirstNameStartingWith(@PathVariable String firstName){
        return studentService.getStudentByFirstNameStartingWith(firstName);
    }

    @GetMapping("/jsn/fname-endingwith/{firstName}")
    public List<Student> getStudentByFirstNameEndingWith(@PathVariable String firstName){
        return studentService.getStudentByFirstNameEndingWith(firstName);
    }

    @GetMapping("/jsn/age-between")
    public List<Student> getStudentByAgeBetween(@RequestParam("agemin") Integer ageMin, @RequestParam("agemax") Integer ageMax){
        return studentService.getStudentByAgeBetween(ageMin, ageMax);
    }

    @GetMapping("/jsn/addrress-nesting/{address1}/{city}/{age}")
    public List<Student> getStudentAddress1AndCityAndAgeGreaterThan(
            @PathVariable String address1,
            @PathVariable String city,
            @PathVariable Integer age){
        return studentService.getStudentAddress1AndCityAndAgeGreaterThan(address1,city,age);
    }


    // QUERY-DSL METHODS ------------------------------------------------------------------------------------------------

    @GetMapping("/qrydsl/allStudent/{pageNumber}/{pageSize}/{name}")
    public List<Student> getAllStudent(@PathVariable Integer pageNumber, @PathVariable Integer pageSize,@PathVariable String name) {
        return studentService.getAllStudent(pageNumber,pageSize,name);
    }


    @GetMapping("/qrydsl/fname/{firstName}")
    public List<Student> getStudentListByFirstName(@PathVariable String firstName){
        return studentService.getStudentListByFirstName(firstName);
    }

    @GetMapping("/qrydsl/fname-startingwith/{firstName}")
    public List<Student> getStudentStartingWith(@PathVariable String firstName){
        return studentService.getStudentStartingWith(firstName);
    }

    @GetMapping("/qrydsl/fname-endingwith/{firstName}")
    public List<Student> getStudentEndingWith(@PathVariable String firstName){
        return studentService.getStudentEndingWith(firstName);
    }

    @GetMapping("/qrydsl/age-between")
    public List<Student> getStudentsBetweenAge(@RequestParam("agemin") Integer ageMin, @RequestParam("agemax") Integer ageMax){
        return studentService.getStudentsBetweenAge(ageMin,ageMax);
    }

    @GetMapping("/qrydsl/multi-predicate-orderspecifer/{fname}/{lname}")
    public List<StudentDTO> getAllStudentByQueryDSLByMatchingNameGenderTotalSpendInBooksOrder(@PathVariable("fname") String matchFname,
                                                                                           @PathVariable("lname") String matchLname){
        return studentService.getAllStudentByQueryDSLByMatchingNameGenderTotalSpendInBooksOrder(matchFname,matchLname);
    }

    // USING QUERY BY EXAMPLE ------------------------------------------------------------------------------------------------

    @GetMapping("/example/fname-startingwith/{matchFname}/{spendInBooks}")
    public List<Student> queryByExampleFnameStartingWith(@PathVariable("matchFname") String matchExampleFname,
                                                         @PathVariable BigDecimal spendInBooks){
        return studentService.queryByExampleFnameStartingWith(matchExampleFname,spendInBooks);
    }

    @GetMapping("/example/casesensitive/{firstName}/{lastName}")
    public List<Student> exampleCaseSensitiveMatch(@PathVariable String firstName, @PathVariable String lastName){
        return studentService.exampleCaseSensitiveMatch(firstName,lastName);
    }

    @GetMapping("/example/transform")
    public List<Student> exampleTransformingMatch(@RequestParam("name") String firstName){
        return studentService.exampleTransformingMatch(firstName);
    }

    // Using Criteria Query with MongoTemplate ------------------------------------------------------------------------------------------------

    @GetMapping("/criteria/fnameByIS/{fname}")
    public List<Student> getStudentFnameByIS(@PathVariable("fname") String firstname){
        return studentService.getStudentFnameByIS(firstname);
    }

    @GetMapping("/criteria/fnameByRegexStarting/{fname}")
    public List<Student> getStudentFirstNameStartingWith(@PathVariable("fname") String firstname){
        return studentService.getStudentFirstNameStartingWith(firstname);
    }

    @GetMapping("/criteria/fnameByRegexEnding/{fname}")
    public List<Student> getStudentsFirstNamesEndingWith(@PathVariable("fname") String firstname){
        return studentService.getStudentsFirstNamesEndingWith(firstname);
    }

    @GetMapping("/criteria/age-between")
    public List<Student> getStudentAgeBetween(@RequestParam("agemin") Integer ageMin, @RequestParam("agemax") Integer ageMax){
        return studentService.getStudentAgeBetween(ageMin,ageMax);
    }

    @GetMapping("/criteria/age-sorted")
    public List<StudentDTO> getStudentAgeSorted(){
        return studentService.getStudentAgeSorted();
    }

    @GetMapping("/criteria/page/{pageNumber}/{pageSize}")
    public List<StudentDTO> searchStudentPageable(@PathVariable Integer pageNumber, @PathVariable Integer pageSize){
        return studentService.searchStudentPageable(pageNumber,pageSize);
    }

    @PutMapping("/update")
    public ResponseEntity<StudentDTO> updateStudent(@RequestBody StudentDTO studentDTO){
        StudentDTO updatedStudent=studentService.updateStudent(studentDTO);
        return new ResponseEntity<>(updatedStudent,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{fname}")
    public DeleteResult deleteStudentByFirstName(@PathVariable String fname){
        return studentService.deleteStudentByFirstName(fname);
    }

    @GetMapping("/projection-result")
    public List<Student> getStudentByProjectionsByFirstName(){
        return studentService.getStudentByProjections();
    }

    @GetMapping("/aggregation")
    public List<Document> getStudentByAggregation(){
        return studentService.getStudentByAggregation();
    }

    @PostMapping("/save")
    public ResponseEntity<StudentDTO> saveStudent(@RequestBody StudentDTO student){
        StudentDTO savedStudent=studentService.saveStudent(student);
        return new ResponseEntity<>(savedStudent,HttpStatus.CREATED);
    }

}
