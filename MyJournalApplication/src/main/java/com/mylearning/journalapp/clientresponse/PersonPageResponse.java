package com.mylearning.journalapp.clientresponse;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
// org.springframework.hateoas.PagedModel<PersonResource>
@Getter
@Setter
public class PersonPageResponse {
    //private List<Person> content;  // List of Person objects (the actual content)
    private org.springframework.hateoas.PagedModel<PersonResource> personPagedModel;  // List of Person objects (the actual content)
    private int pageNumber;        // Current page number
    private int pageSize;          // Size of the page
    private long totalElements;    // Total number of elements
    private int totalPages;        // Total number of pages
    private boolean last;          // Indicator if it's the last page

}
