package org.dailycodebuffer.codebufferspringbootmongodb.config;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Person;
import org.dailycodebuffer.codebufferspringbootmongodb.controller.PersonController;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.CollectionModel;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class extends RepresentationModelAssemblerSupport which is required for Pagination.
 * It converts the Person Entity to the PersonResource Model and has the code for it
 */
@Component
@Slf4j
public class PersonResourceAssembler extends RepresentationModelAssemblerSupport<Person, PersonResource>  {
    public PersonResourceAssembler() {
        super(PersonController.class, PersonResource.class);
    }

    @Override
    public PersonResource toModel(Person person) {
        log.info("PersonResourceAssembler toModel() called");

        PersonResource personResource = instantiateModel(person);
        //PersonResource personResource1 = new PersonResource(); we can also use this code statement to create PersonResource object

        personResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)
                        .getPersonById(person.getPersonId().toHexString()))
                .withSelfRel());

        // Both PersonResource and Person have the same property names. So copy the values from the Entity to the Model
        //BeanUtils.copyProperties(person,personResource);  // we can use this BeanUtils to copy properties

        personResource.setPersonId(person.getPersonId());
        personResource.setFirstName(person.getFirstName());
        personResource.setLastName(person.getLastName());
        personResource.setAge(person.getAge());
        personResource.setHobbies(person.getHobbies());
        personResource.setAddresses(person.getAddresses());

        return personResource;
    }

    @Override
    public CollectionModel<PersonResource> toCollectionModel(Iterable<? extends Person> persons) {
        log.info("PersonResourceAssembler toCollectionModel() called");
        CollectionModel<PersonResource> personModel = super.toCollectionModel(persons);
        personModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)
                .getAllPersons()).withSelfRel());
                return personModel;
    }
}