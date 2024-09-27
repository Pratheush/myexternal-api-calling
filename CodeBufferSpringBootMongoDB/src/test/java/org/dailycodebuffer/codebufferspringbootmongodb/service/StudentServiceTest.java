package org.dailycodebuffer.codebufferspringbootmongodb.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.bson.Document;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.controller.StudentController;
import org.dailycodebuffer.codebufferspringbootmongodb.converter.DocumentConverter;
import org.dailycodebuffer.codebufferspringbootmongodb.converter.StudentConverter;
import org.dailycodebuffer.codebufferspringbootmongodb.dto.StudentDTO;
import org.dailycodebuffer.codebufferspringbootmongodb.exceptions.StudentNotFoundException;
import org.dailycodebuffer.codebufferspringbootmongodb.matcher.StudentDTOMatcher;
import org.dailycodebuffer.codebufferspringbootmongodb.repository.StudentRepository;
import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentConverter studentConverter;

    @Mock
    private DocumentConverter documentConverter;

    //@Mock
    private MongoTemplate mongoTemplate;

    private static List<Student> students=new ArrayList<>();

    // we can use @Mock to create and inject mocked instances without having to call Mockito.mock manually.
    // in below method creating mocks manually and injecting the mock through constructor
    @BeforeEach
    public void setUp() {
        System.out.println("setUp @BeforeEach");
        mongoTemplate = Mockito.mock(MongoTemplate.class);
        studentConverter = Mockito.mock(StudentConverter.class);
        studentRepository=Mockito.mock(StudentRepository.class);
        documentConverter = Mockito.mock(DocumentConverter.class);
        studentService = new StudentService(studentRepository,mongoTemplate,studentConverter); // assuming YourClass is the class containing the updateStudent method
    }

    @BeforeAll
    public static void setUpStudentList(){
        System.out.println("setUpStudentList @BeforeAll");

        Student s1=Student.builder()
                .studentId("657ae2488a36df4ec5c2efc0")
                .firstName("Rajat")
                .lastName("Kumar")
                .age(23)
                .gender(Gender.MALE)
                .email("rajatkumar@gmail.com")
                .favSubjects(Arrays.asList("Coding", "Chess"))
                .totalSpendInBooks(BigDecimal.valueOf(3214))
                .address(new Address("Karnataka","Marathahalli", "Bangalore"))
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        Student s2=Student.builder()
                .studentId("657ae2488a36df4ec5c2efb9")
                .firstName("Rajat")
                .lastName("Sharma")
                .age(23)
                .gender(Gender.MALE)
                .email("rajatsharma@gmail.com")
                .favSubjects(Arrays.asList("Coding", "Chess"))
                .totalSpendInBooks(BigDecimal.valueOf(2214))
                .address(new Address("Karnataka","Marathahalli", "Bangalore"))
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        students.add(s1);
        students.add(s2);
    }

    @Test
    @DisplayName("test FindBy FirstName & Age Order By TotalSpendInBooks in Desc")
    void  testFindByFirstNameAndAgeOrderByTotalSpendInBooksDesc(){

        // Arrange
        String firstName = "Ravi";
        Integer age = 28;

        Student student1 =new Student();
        student1.setFirstName("Ravi");
        student1.setLastName("Kumar");
        student1.setAge(28);

        List<Student> studentList=new ArrayList<>();
        studentList.add(student1);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName("Ravi");
        studentDTO.setLastName("Kumar");
        studentDTO.setAge(28);

        // Mock the behavior of studentRepository
        Mockito.when(studentRepository.findByFirstNameAndAgeOrderByTotalSpendInBooksDesc(Mockito.anyString(),Mockito.anyInt())).thenReturn(studentList);

        // Mock the behavior of studentConverter
        Mockito.when(studentConverter.convertStudentToDTO(Mockito.any(Student.class))).thenReturn(studentDTO);

        // Act
        List<StudentDTO> studentDtoList = studentService.findByFirstNameAndAgeOrderByTotalSpendInBooksDesc(firstName,age);

        // Assert
        assertEquals(studentDTO.getFirstName(),studentDtoList.getFirst().getFirstName());
        assertEquals(1,studentDtoList.size());

        // Verify that the methods were called with the expected arguments
        Mockito.verify(studentRepository,Mockito.times(1)).findByFirstNameAndAgeOrderByTotalSpendInBooksDesc(firstName,age);
        Mockito.verify(studentConverter,Mockito.times(1)).convertStudentToDTO(Mockito.any(Student.class)); // We can specify the exact student object if needed
    }


    @Test
    @DisplayName("Save student test")
    void testSaveStudent(){

        Student student = new Student();
        student.setLastName("Ravi");
        student.setLastName("Sharma");
        student.setAge(25);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName("Ravi");
        studentDTO.setLastName("Sharma");
        studentDTO.setAge(25);

        // Mock the behaviour of studentConverter converting dto to student
        Mockito.when(studentConverter.convertDtoToStudent(Mockito.any(StudentDTO.class))).thenReturn(student);
        // Mock the behaviour of studentRepository
        Mockito.when(studentRepository.save(Mockito.any(Student.class))).thenReturn(student);
        // Mock the behaviour of studentConverter converting student to dto
        Mockito.when(studentConverter.convertStudentToDTO(Mockito.any(Student.class))).thenReturn(studentDTO);

        // Act
        StudentDTO result = studentService.saveStudent(studentDTO);

        // Assert
        assertEquals(studentDTO.getFirstName(), result.getFirstName(),"First name is not Ravi");
        assertNotNull(result,"result is null");
        assertEquals(studentDTO.getLastName(), result.getLastName(),"Last Name is not Sharma");

        // Verify that the methods were called with the expected arguments
        Mockito.verify(studentRepository,Mockito.times(1)).save(Mockito.any(Student.class));
        Mockito.verify(studentConverter,Mockito.times(1)).convertDtoToStudent(Mockito.any(StudentDTO.class));
        Mockito.verify(studentConverter,Mockito.times(1)).convertStudentToDTO(student);
    }

    @Test
    @DisplayName(" test Update Student WIthout Exception THrown")
    void testUpdateStudent_Success(){

        // Mock converted student
        Student convertedStudent = new Student();
        convertedStudent.setLastName("Ravi");
        convertedStudent.setLastName("Sharma");
        convertedStudent.setAge(25);

        // // Mock input data
        StudentDTO inputStudentDTO = new StudentDTO();
        inputStudentDTO.setFirstName("Ravi");
        inputStudentDTO.setLastName("Sharma");
        inputStudentDTO.setAge(25);

        // Mock expected Student
        Student expectedResult = new Student(); // Set your expected result here
        expectedResult.setFirstName("Ravi");
        expectedResult.setLastName("Sharma");
        expectedResult.setAge(25);

        // Mock expected DTO
        StudentDTO expectedDTO = new StudentDTO(); // Set your expected DTO here
        expectedDTO.setFirstName("Ravi");
        expectedDTO.setLastName("Sharma");
        expectedDTO.setAge(25);

        // Mock the behavior of StudentConverter  DTO to Student
        Mockito.when(studentConverter.convertDtoToStudent(inputStudentDTO)).thenReturn(convertedStudent);
        // Mock the behavior of MongoTemplate
        Mockito.when(mongoTemplate.findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class))).thenReturn(expectedResult);
        // Mock the behavior of StudentConverter Student to DTO
        Mockito.when(studentConverter.convertStudentToDTO(expectedResult)).thenReturn(expectedDTO);

        // Act :: Call the method to be tested
        StudentDTO resultDTO = studentService.updateStudent(inputStudentDTO);

        // Assert
        assertEquals(inputStudentDTO.getFirstName(), resultDTO.getFirstName());
        assertNotNull(resultDTO);
        assertEquals(inputStudentDTO.getLastName(),resultDTO.getLastName());

        // Verify interactions
        Mockito.verify(studentConverter,Mockito.times(1)).convertDtoToStudent(inputStudentDTO);
        Mockito.verify(mongoTemplate,Mockito.times(1)).findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class));
        Mockito.verify(studentConverter,Mockito.times(1)).convertStudentToDTO(expectedResult);

    }

    @Test
    @DisplayName(" test Update Student WIth Exception THrown")
    void testUpdateStudent_Failure(){

        // Mock converted student
        Student convertedStudent = new Student();
        convertedStudent.setFirstName("Ravi");
        convertedStudent.setLastName("Sharma");
        convertedStudent.setAge(25);

        // // Mock input data
        StudentDTO inputStudentDTO = new StudentDTO();
        inputStudentDTO.setFirstName("Ravi");
        inputStudentDTO.setLastName("Sharma");
        inputStudentDTO.setAge(25);


        // Mock the behavior of StudentConverter  DTO to Student
        Mockito.when(studentConverter.convertDtoToStudent(inputStudentDTO)).thenReturn(convertedStudent);
        // Mock the behavior of MongoTemplate
        Mockito.when(mongoTemplate.findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class))).thenReturn(null);

        // Act :: Call the method to be tested
        //StudentDTO resultDTO = studentService.updateStudent(inputStudentDTO);
        /*try { studentService.updateStudent(inputStudentDTO);
            fail(new StudentNotFoundException("No Student Found"));
        }catch (StudentNotFoundException ex){
            System.err.println(ex.getMessage());
        }*/

        // Assert
        assertThrowsExactly(StudentNotFoundException.class,()-> studentService.updateStudent(inputStudentDTO),"StudentNotFoundException Not Thrown");

        final Exception ex =assertThrows(StudentNotFoundException.class,()-> studentService.updateStudent(inputStudentDTO),"StudentNotFoundException Not Thrown");
        assertThat(ex, CoreMatchers.instanceOf(StudentNotFoundException.class));
        assertThat(ex.getMessage(),CoreMatchers.containsStringIgnoringCase("No Student Found"));

        // Verify interactions
        Mockito.verify(studentConverter,Mockito.times(2)).convertDtoToStudent(inputStudentDTO);
        Mockito.verify(mongoTemplate,Mockito.times(2)).findAndModify(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.eq(Student.class));
        Mockito.verify(studentConverter, Mockito.never()).convertStudentToDTO(Mockito.any(Student.class));

    }

    @Test
    void testGetStudentAgeSorted(){

        List<Student> studentList = new ArrayList<Student>();

        Student st = new Student();
        st.setFirstName("John");
        st.setLastName("Wright");
        st.setAge(28);

        studentList.add(st);

        Document studDoc=new Document();
        studDoc.append("firstName",st.getFirstName());
        studDoc.append("lastName",st.getLastName());
        studDoc.append("age",st.getAge());

        StudentDTO convertStudentToDTO = new StudentDTO();
        convertStudentToDTO.setFirstName(st.getFirstName());
        convertStudentToDTO.setLastName(st.getLastName());
        convertStudentToDTO.setAge(st.getAge());

        AggregationResults<Student> aggregationResults1=new AggregationResults<>(studentList,studDoc);

        Mockito.when(studentConverter.convertStudentToDTO(Mockito.any(Student.class))).thenReturn(convertStudentToDTO);
        Mockito.when(mongoTemplate.aggregate(Mockito.any(Aggregation.class),Mockito.eq(Student.class),Mockito.eq(Student.class)))
                .thenReturn(aggregationResults1);


        // Call the method under test
        List<StudentDTO> result = studentService.getStudentAgeSorted();

        // assert
        assertEquals(result.size(),studentList.size());
        assertEquals(result.getFirst().getFirstName(),studentList.getFirst().getFirstName());

        // Verify
        Mockito.verify(mongoTemplate,Mockito.times(1)).aggregate(Mockito.any(Aggregation.class),Mockito.eq(Student.class),Mockito.eq(Student.class));
        Mockito.verify(studentConverter,Mockito.times(1)).convertStudentToDTO(st);
    }

    @Test
    void testsearchStudentPageable(){

       Student student1 = new Student();
       student1.setFirstName("Pamila");
       student1.setLastName("Rotche");
       student1.setAge(27);
       student1.setGender(Gender.FEMALE);

       Student student2 = new Student();
       student2.setFirstName("Evika");
       student2.setLastName("Baeilfast");
       student2.setAge(26);
       student2.setGender(Gender.FEMALE);

       List<Student> studentsList=new ArrayList<>();
        studentsList.add(student1);
        studentsList.add(student2);

       StudentDTO studentDTO1=new StudentDTO();
       studentDTO1.setFirstName(student1.getFirstName());
       studentDTO1.setLastName(student1.getLastName());
       studentDTO1.setAge(student1.getAge());
       studentDTO1.setGender(student1.getGender());

        StudentDTO studentDTO2=new StudentDTO();
        studentDTO2.setFirstName(student1.getFirstName());
        studentDTO2.setLastName(student1.getLastName());
        studentDTO2.setAge(student1.getAge());
        studentDTO2.setGender(student1.getGender());

        List<StudentDTO> studentDTOS=new ArrayList<>();
        studentDTOS.add(studentDTO1);
        studentDTOS.add(studentDTO2);

        Mockito.when(mongoTemplate.find(Mockito.any(Query.class),Mockito.eq(Student.class))).thenReturn(studentsList);
        Mockito.when(mongoTemplate.count(Mockito.any(Query.class),Mockito.eq(Student.class))).thenReturn(2L);
        Mockito.when(studentConverter.convertStudentToDTO(Mockito.any(Student.class))).thenReturn(studentDTO1,studentDTO2);

        // Call that method under test
        List<StudentDTO> resultDtoList = studentService.searchStudentPageable(1, 1);

        // assert
        assertNotNull(resultDtoList);
        assertEquals(resultDtoList.size(),studentsList.size());
        assertEquals(resultDtoList.getFirst().getFirstName(),studentsList.getFirst().getFirstName());

        // Verify
        Mockito.verify(mongoTemplate, Mockito.times(1)).find(Mockito.any(Query.class),Mockito.eq(Student.class));
        Mockito.verify(mongoTemplate,Mockito.times(1)).count(Mockito.any(Query.class),Mockito.eq(Student.class));
        Mockito.verify(studentConverter,Mockito.times(2)).convertStudentToDTO(Mockito.any(Student.class));

    }

    @Test
    void testgetAllStudentByQueryDSLByMatchingNameGenderTotalSpendInBooksOrder(){

        Student student1 = new Student();
        student1.setFirstName("Erica");
        student1.setLastName("Baeilfast");
        student1.setAge(27);
        student1.setGender(Gender.FEMALE);
        student1.setTotalSpendInBooks(BigDecimal.valueOf(4300));

        Student student2 = new Student();
        student2.setFirstName("Pamerica");
        student2.setLastName("Baeilfast");
        student2.setAge(26);
        student2.setGender(Gender.FEMALE);
        student2.setTotalSpendInBooks(BigDecimal.valueOf(4100));

        List<Student> studentsList=new ArrayList<>();
        studentsList.add(student1);
        studentsList.add(student2);

        StudentDTO studentDto1 = new StudentDTO();
        studentDto1.setFirstName("Erica");
        studentDto1.setLastName("Baeilfast");
        studentDto1.setAge(27);
        studentDto1.setGender(Gender.FEMALE);
        studentDto1.setTotalSpendInBooks(BigDecimal.valueOf(4300));

        StudentDTO studentDto2 = new StudentDTO();
        studentDto2.setFirstName("Pamerica");
        studentDto2.setLastName("Baeilfast");
        studentDto2.setAge(26);
        studentDto2.setGender(Gender.FEMALE);
        studentDto2.setTotalSpendInBooks(BigDecimal.valueOf(4100));

        Mockito.when(studentConverter.convertStudentToDTO(Mockito.any(Student.class))).thenReturn(studentDto1,studentDto2);
        Mockito.when(studentRepository.findAll(Mockito.any(Predicate.class),Mockito.any(OrderSpecifier.class))).thenReturn(studentsList);

        // call the method to test
        List<StudentDTO>  resultStudentDTOs = studentService.getAllStudentByQueryDSLByMatchingNameGenderTotalSpendInBooksOrder("rica", "baeilfast");

        // assert
        assertNotNull(resultStudentDTOs);
        assertEquals(studentsList.size(), resultStudentDTOs.size());
        assertEquals(studentsList.getFirst().getTotalSpendInBooks(), resultStudentDTOs.getFirst().getTotalSpendInBooks());

        // verify
        Mockito.verify(studentConverter,Mockito.times(2)).convertStudentToDTO(Mockito.any(Student.class));
        Mockito.verify(studentRepository,Mockito.times(1)).findAll(Mockito.any(Predicate.class),Mockito.any(OrderSpecifier.class));
    }


    @Test
    @DisplayName("Delete student With Student Not Found Exception")
    void testdeleteStudentById_StudentNotFound(){

        Mockito.when(studentRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        // Act and Assert
        StudentNotFoundException ex=assertThrowsExactly(StudentNotFoundException.class,()->{
            studentService.deleteStudentById("1");
        });

        // Assert
        assertEquals("No Student Found By given StudentId",ex.getMessage());

        // verify
        Mockito.verify(studentRepository,Mockito.times(1)).findById(Mockito.anyString());
        // Optionally, you can verify that deleteById was not called
        Mockito.verify(studentRepository,Mockito.never()).deleteById(Mockito.anyString());
    }

    @Test
    @DisplayName("Delete Student With Success")
    void testdeleteStudentById_StudentFound(){

        Student std=new Student();
        std.setStudentId("657ae2488a36df4ec5c2efc0");
        std.setFirstName("John");
        std.setLastName("Weir");
        std.setAge(29);
        std.setGender(Gender.MALE);

        //AtomicReference<Boolean> verifyMethodCalled = new AtomicReference<>(false);
        AtomicBoolean verifyMethodCalled = new AtomicBoolean(false);

        Mockito.when(studentRepository.findById(Mockito.anyString())).thenReturn(Optional.of(std));

        // Use Mockito.doAnswer for deleteById
        /*Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // Custom behavior for deleteById
                Object[] arguments = invocation.getArguments();
                String deletedStudentId = (String) arguments[0];
                //Boolean verifyMethodCalled=true;
                // You can perform additional assertions on deletedStudentId if needed
                return null;
            }
        }).when(studentRepository).deleteById(Mockito.anyString());*/

        Mockito.doAnswer(invocationOnMock -> {
            // we can do whatever we want here, and it will be executed when the method is called
            // if method had any arguments we can capture them here like we did above
            Object[] arguments = invocationOnMock.getArguments();
            String deletedStudentId = (String) arguments[0];
            //System.out.println("args:::"+Arrays.toString(arguments));
            //System.out.println("deletedStudentId::::"+deletedStudentId);
            assertNotNull(arguments,"arguments are not null");

            verifyMethodCalled.set(true);
            return null;
        }).when(studentRepository).deleteById(Mockito.anyString());

        // Act and Assert
        studentService.deleteStudentById(std.getStudentId());

        assertTrue(verifyMethodCalled.get());

        // verify
        Mockito.verify(studentRepository,Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verify(studentRepository,Mockito.times(1)).deleteById(Mockito.anyString());
    }

    @Test
    void testStudentMatcher(){
        Student student1=students.getFirst();
        StudentDTO studentDTO = getStudentDTO();

        StudentDTOMatcher dtOMatcher = new StudentDTOMatcher(
                student1.getFirstName(),
                student1.getLastName(),
                student1.getAge(),
                student1.getEmail(),
                student1.getGender(),
                student1.getAddress(),
                student1.getFavSubjects(),
                student1.getTotalSpendInBooks(),
                student1.getCreatedAt(),
                student1.getUpdatedAt());

        //studentRepository.save(student1);

        Mockito.when(studentConverter.convertDtoToStudent(studentDTO)).thenReturn(student1);
        Mockito.when(studentRepository.save(students.getFirst())).thenReturn(student1);
        Mockito.when(studentConverter.convertStudentToDTO(student1)).thenReturn(studentDTO);

        studentService.saveStudent(studentDTO);

        Mockito.verify(studentConverter).convertDtoToStudent(Mockito.argThat(dtOMatcher));
        Mockito.verify(studentRepository,Mockito.times(1)).save(student1);
        Mockito.verify(studentConverter).convertStudentToDTO(student1);

    }

    @NotNull
    private static StudentDTO getStudentDTO() {
        Student student1=students.getFirst();

        StudentDTO studentDTO=new StudentDTO();
        studentDTO.setStudentId(student1.getStudentId());
        studentDTO.setFirstName(student1.getFirstName());
        studentDTO.setLastName(student1.getLastName());
        studentDTO.setEmail(student1.getEmail());
        studentDTO.setAge(student1.getAge());
        studentDTO.setGender(student1.getGender());
        studentDTO.setFavSubjects(student1.getFavSubjects());
        studentDTO.setTotalSpendInBooks(student1.getTotalSpendInBooks());
        studentDTO.setAddress(student1.getAddress());
        studentDTO.setCreatedAt(student1.getCreatedAt());
        studentDTO.setUpdatedAt(student1.getUpdatedAt());
        return studentDTO;
    }

}
