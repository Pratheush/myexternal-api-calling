package org.dailycodebuffer.codebufferspringbootmongodb;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Address;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Gender;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.dailycodebuffer.codebufferspringbootmongodb.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;


/**
 * This ensures that Spring Data's pageable support is enabled, and HATEOAS is configured to use HAL format.
 * By enabling Spring Data Web and HATEOAS support through the @EnableSpringDataWebSupport and @EnableHypermediaSupport annotations,
 * you can resolve the issue with autowiring PagedResourcesAssembler. Once configured,
 * Spring will inject this bean, allowing you to handle paginated responses with HATEOAS metadata and links.
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO) // Enables Pageable and Sort as method arguments
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL) // Enables HATEOAS support
public class CodeBufferSpringBootMongoDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeBufferSpringBootMongoDbApplication.class, args);
    }


    /*@Bean
    CommandLineRunner init(StudentRepository studentRepository){
        return args -> {
            List<Student> students = IntStream.range(1,25)
                    .mapToObj(count -> {
                        List<String> subjects = new ArrayList<>();
                        subjects.add("Coding");
                        subjects.add("Watching Kdramas");
                        subjects.add("Playing Chess");
                        return Student.builder()
                                .firstName("Raj " + count)
                                .lastName("Sharma " + count)
                                .age(ThreadLocalRandom.current().nextInt(11, 28))
                                .email("raj" + count + "@gmail.com")
                                .gender(count % 2 == 0 ? Gender.FEMALE : Gender.MALE)
                                .address(Address.builder()
                                        .address1("Karnataka " + count)
                                        .address2("Marathahalli " + count)
                                        .city("Bangalore " + count)
                                        .build())
                                .favSubjects(subjects)
                                .totalSpendInBooks(BigDecimal.valueOf((4563 + (count * 10L + 45 + count))))
                                .createdAt(LocalDateTime.now())
                                .build();
                    }).toList();
            studentRepository.saveAll(students);
        };
    }*/
}
