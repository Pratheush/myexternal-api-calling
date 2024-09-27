package org.dailycodebuffer.codebufferspringbootmongodb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import org.bson.types.ObjectId;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.dailycodebuffer.codebufferspringbootmongodb.config.PersonResource;
import org.dailycodebuffer.codebufferspringbootmongodb.config.PersonResourceAssembler;
import org.dailycodebuffer.codebufferspringbootmongodb.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * In your PersonController, you can use PagedResourcesAssembler to return the paginated results
 */
@Tag(name = "Person", description="The Person Management Service")
@RestController
@RequestMapping("/api/person")
@Profile("codebuffer")
@Slf4j
public class PersonController {
    private final PersonService personService;
    private final PagedResourcesAssembler<Person> pagedResourcesAssembler;
    private final PersonResourceAssembler personResourceAssembler;


    @Autowired
    public PersonController(PersonService personService, PagedResourcesAssembler<Person> pagedResourcesAssembler, PersonResourceAssembler personResourceAssembler) {
        this.personService = personService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.personResourceAssembler = personResourceAssembler;
    }

    @Operation(
            summary = "Create a new Person",
            description = "Create a new Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Save Person"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created", content = {@Content(schema = @Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())})
    })
    @PostMapping
    public ObjectId save(@RequestBody @Parameter(description = "Create Person") Person person) {
        return personService.save(person);
    }

    /**
     * @param name Filter for the first Name if required
     * @return List of filtered person based on firstName
     */
    @Operation(
            summary = "Get a Person from the database using Person Name",
            description = "Get a Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Get Person By Person FirstName"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Successfully retrieved", content = {@Content(schema = @Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @GetMapping
    public List<Person> getPersonStartWith(@RequestParam("name") @Parameter(name = "FirstName", description = "Search Person by Person FirstName", example = "Raj") String name) {
        return personService.getPersonStartWith(name);
    }

    @Operation(
            summary = "Deletes a person from the database",
            description = "Deletes a person from the database using the Id",
            tags = {"Delete person"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode ="200", description="Successfully Deleted"),
            @ApiResponse(responseCode = "404", description="Not Found - The Person was not found"),
            @ApiResponse(responseCode = "500", description="Internal Server Error"),
            @ApiResponse(responseCode = "400", description="Bad Request")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") @Parameter(name = "PersonId", description = "Search Person by Person Id", example = "65817c98286622a8d2bcbf4c") ObjectId id) {
        log.info("PersonController delete() called with id: " + id);
        Boolean deleted = personService.delete(id);
        if (Boolean.TRUE.equals(deleted)) return ResponseEntity.noContent().build();
        else return ResponseEntity.badRequest().build();
    }

    @Operation(
            summary = "Get a Person from the database using Person Age",
            description = "Get a Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Get person By Age"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Successfully retrieved", content = {@Content(schema = @Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @GetMapping("/age")
    public List<Person> getByPersonAge(@RequestParam(value = "min") @Parameter(name = "Minimum Age", description = "Search Person by Person Minimum Age", example = "23") Integer minAge,
                                       @RequestParam(value = "max") @Parameter(name = "Maximum Age", description = "Search Person by Person Maximum Age", example = "30") Integer maxAge) {
        return personService.getByPersonAge(minAge,maxAge);
    }

    @Operation(
            summary = "Search a Person from the database based on Person name, Person Minimum age, and Person Maximum age, City",
            description = "Get a Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Search Person"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Successfully retrieved", content = {@Content(schema = @Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @Parameters(value ={
            @Parameter(name = "Name", description = "Search Person by Person's FistName", example = "Raj"),
            @Parameter(name = "Minimum Age", description = "Search Person by Person Minimum Age", example = "23"),
            @Parameter(name = "Maximum Age", description = "Search Person by Person Maximum Age", example = "33"),
            @Parameter(name = "City", description = "Search Person by City", example = "Bangalore"),
            @Parameter(name = "Page", description = "Page number, starting from 0", example = "0"),
            @Parameter(name = "Size", description = "Number of items per page", example = "5")
    })
    @GetMapping(value = "/search",produces = { "application/hal+json" })
    public ResponseEntity<org.springframework.hateoas.PagedModel<PersonResource>> searchPerson(

            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        log.info("PersonController searchPerson called");
        log.info("PersonController >> name : {}, minAge : {}, maxAge : {}, city : {}, page : {}, size : {}", name, minAge, maxAge,city,page,size);

        // create Pageable object using the page and size
        Pageable pageable
                = PageRequest.of(page,size);

        // Fetch the paginated list of persons
        Page<Person> personPage = personService.search(name, minAge, maxAge, city, pageable);

        List<Person> personList = personPage.getContent();
        log.info("PersonController searchPerson personList :: {}", personList);

       // Using the assembler we are converting the Page<Person> to PagedModel<PersonResource> by calling the toModel() method
        // Convert Person to PersonResource
        // Convert Person to PersonResource using PersonResourceAssembler
        org.springframework.hateoas.PagedModel<PersonResource> pagedModel  = pagedResourcesAssembler
                .toModel(personPage, personResourceAssembler);

        // this logging is just to know that I am getting response after search in Db
        log.info("PersonController pagedModel :: {}", pagedModel);
        // Convert the Page object into PagedModel
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @Operation(
            summary = "Get an Oldest Person from the database using Person Age And City",
            description = "Get a Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Get person By OLD Age ANd CIty"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = " Get An Oldest Person By Age and City",content={@Content(schema=@Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @GetMapping("/oldestPerson")
    public List<Document> getOldestPerson() {
        return personService.getOldestPersonByCity();
    }

    @Operation(
            summary = "Get List Of Person from the database By City",
            description = "Get List Of Person's with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"PersonList By City"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = " Get List Of Person By City",content={@Content(schema=@Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @GetMapping("/populationByCity")
    public List<Document> getPopulationByCity() {
        return personService.getPopulationByCity();
    }

    @Operation(
            summary = "Update a Person",
            description = "Update a Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Update Person"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created", content = {@Content(schema = @Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())})
    })
    @PutMapping
    public ResponseEntity<?> updatePerson(@RequestBody @Parameter(description = "Update Person") Person person,
                                       @RequestParam(value = "personId")
                               @Parameter(name="personId",description = "Search Person By Id to Update Person Details. Person Id in ObjectId format", example = "66e8d7943d5cf20c5e811501") String personId) {
        log.info("PersonController updatePerson called with person {} and personId {}",person,personId);
        // Check if personId is a valid ObjectId
        if (ObjectId.isValid(personId)) {
            ObjectId objectIdPersonId = new ObjectId(personId);
            Person updatedPerson = personService.updatePerson(person, objectIdPersonId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedPerson);
        }else throw new IllegalArgumentException("Invalid ObjectId");

    }

    @Operation(
            summary = "Get an Person from the database using Person Age And Name",
            description = "Get a Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Get person By Age ANd Name"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = " Get An Oldest Person By Age and City",content={@Content(schema=@Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @GetMapping("/{name}/{age}")
    public Person getPersonByNameAndAgePathVariable(
            @PathVariable(value = "name") @Parameter(name = "firstName", description = "Search Person by Person Firstname", example = "Rajji") String firstName,
            @PathVariable(value = "age") @Parameter(name = "age", description = "Search Person by Person Age", example = "33") Integer age
    ) {
        return personService.getPersonByNameAndAgePathVariable(firstName,age);
    }

    @Operation(
            summary = "Get an Person from the database using Person Id",
            description = "Get a Person with PersonId",
            tags = {"Get person By PersonId"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = " Get an Person from the database using Person Id",content={@Content(schema=@Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    // any pathVariable value would work here in place of {id} we can use {personId} both will work
    @GetMapping("/{personId}")
    public Person getPersonById(@PathVariable("personId") String personId) {
        log.info("PersonController getPersonById() called with personId :: {}",personId);
        //return personService.getPersonById(new ObjectId(personId));
        // Check if personId is a valid ObjectId
        if (ObjectId.isValid(personId)) {
            ObjectId objectIdPersonId = new ObjectId(personId);
            log.info("PersonController getPersonById personId >>> {}", personId);
            log.info("PersonController getPersonById objectIdPersonId >>> {}", objectIdPersonId);
            return personService.getPersonById(objectIdPersonId);
        }else throw new IllegalArgumentException(" getPersonById() Invalid ObjectId");
    }

    @Operation(
            summary = "Get All Person from the database",
            description = "Get All Person",
            tags = {"Get All person from DB "}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = " Get All Person from the database",content={@Content(schema=@Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @GetMapping(value = "/all-person")
    //public List<Person> getAllPersons() {
    //public CollectionModel<PersonResource> getAllPersons() {
    public List<PersonResource> getAllPersons() {
        log.info("PersonController getAllPersons() called");
        List<Person> allPersons = personService.getAllPersons();

        List<PersonResource> personResourceList = allPersons.stream().map(PersonResource::new).toList();

        /**
         * linkTo(CustomerController.class).slash(customer.getCustomerId()).withSelfRel();
         * the linkTo() method inspects the controller class and obtains its root mapping
         * the slash() method adds the customerId value as the path variable of the link
         * finally, the withSelfMethod() qualifies the relation as a self-link
         */
        for(PersonResource personResource : personResourceList){
            ObjectId personId = personResource.getPersonId();
            // using slash() to create the Link with WebMvcLinkBuilder
            //Link selfLink = WebMvcLinkBuilder.linkTo(PersonController.class).slash(personId).withSelfRel();

            // Links to Controller Methods
            Link selfLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(PersonController.class).getPersonById(personId.toHexString())
            ).withRel(personResource.getFirstName()+"> link");
            personResource.add(selfLink);
        }

        /**
         * At the bottom of the web page because of the commented below code this following link would show up
         * "_links": {
         *         "self": {
         *             "href": "http://localhost:8081/api/person"
         *         }
         *     }
         */
        /*Link link = WebMvcLinkBuilder.linkTo(PersonController.class).withSelfRel();
        CollectionModel<PersonResource> personCollectionModel = CollectionModel.of(personResourceList,link);
        return personCollectionModel;*/
        return personResourceList;

    }

    @PostMapping("/create-person-on-status")
    public ResponseEntity<Person> createPersonOnStatus(@RequestBody Person person){
        log.info("PersonController createPersonOnStatus called");
        Person personOnStatus = personService.createPersonOnStatus(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(personOnStatus);
    }

    @Operation(
            summary = "Search a Person from the database based on Person name, Person Minimum age, and Person Maximum age, City",
            description = "Get a Person with PersonId,Person First Name,Person Last Name,Person Age,Person Hobbies and Person Address(Address1,Address2,City)",
            tags = {"Search Person"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Successfully retrieved", content = {@Content(schema = @Schema(implementation = Person.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - The Person was not found", content = {@Content(schema=@Schema())})
    })
    @Parameters(value ={
            @Parameter(name = "Name", description = "Search Person by Person's FistName", example = "Raj"),
            @Parameter(name = "Minimum Age", description = "Search Person by Person Minimum Age", example = "23"),
            @Parameter(name = "Maximum Age", description = "Search Person by Person Maximum Age", example = "33"),
            @Parameter(name = "City", description = "Search Person by City", example = "Bangalore"),
            @Parameter(name = "Page", description = "Page number, starting from 0", example = "0"),
            @Parameter(name = "Size", description = "Number of items per page", example = "5")
    })
    @GetMapping(value = "/search-no-hateoas",produces = { "application/hal+json" })
    public ResponseEntity<Page<Person>> searchPersonWithoutHateoas(

            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        log.info("PersonController searchPersonWithoutHateoas called");
        log.info("PersonController searchPersonWithoutHateoas >> name : {}, minAge : {}, maxAge : {}, city : {}, page : {}, size : {}", name, minAge, maxAge,city,page,size);

        // create Pageable object using the page and size
        Pageable pageable
                = PageRequest.of(page,size);

        // Fetch the paginated list of persons
        Page<Person> personPage = personService.search(name, minAge, maxAge, city, pageable);

        List<Person> personList = personPage.getContent();
        log.info("PersonController searchPersonWithoutHateoas personList :: {}", personList);

        return new ResponseEntity<>(personPage, HttpStatus.OK);
    }

}
