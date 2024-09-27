## Learn to build automatic pagination links in Spring Hateaos application using PagedModel and PagedResourcesAssembler classes.


* MAVEN DEPENDENCIES :::
```
<!-- Spring HATEOAS -->
        <!-- To implement a stable JSON structure for paginated responses using Spring Data's PagedModel and Spring HATEOAS, along with PagedResourcesAssembler -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-hateoas</artifactId>
        </dependency>
        <!-- Spring Data Web (for pageable support) -->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-commons</artifactId>
        </dependency>  
```

1. First thing, we need to use PagingAndSortingRepository repository which provides methods to retrieve entities using the pagination and sorting abstraction.

2. Pagination with PagedModel using PagedResourcesAssembler:
* To enable automatic pagination links, we must use PagedModel provided by spring hateoas module which helps in creating representations of pageable collections.
* PagedResourcesAssembler accepts the JPA entities list, and converts it to PagedModel.
* Additionally, we can use RepresentationModelAssembler to convert JPA entities into CollectionModel having custom resource representation.
* Finally, PagedModel is returned as the API response from the REST controller.


The AlbumModelAssembler converts a JPA entity to a DTO object (entity and collection representations). i.e. it converts AlbumEntity to AlbumModel.
```
@Component
public class AlbumModelAssembler 
  extends RepresentationModelAssemblerSupport<AlbumEntity, AlbumModel> {
 
  public AlbumModelAssembler() {
    super(WebController.class, AlbumModel.class);
  }
 
  @Override
  public AlbumModel toModel(AlbumEntity entity) 
  {
    AlbumModel albumModel = instantiateModel(entity);
     
    albumModel.add(linkTo(
        methodOn(WebController.class)
        .getActorById(entity.getId()))
        .withSelfRel());
     
    albumModel.setId(entity.getId());
    albumModel.setTitle(entity.getTitle());
    albumModel.setDescription(entity.getDescription());
    albumModel.setReleaseDate(entity.getReleaseDate());
    albumModel.setActors(toActorModel(entity.getActors()));
    return albumModel;
  }
   
  @Override
  public CollectionModel<AlbumModel> toCollectionModel(Iterable<? extends AlbumEntity> entities) 
  {
    CollectionModel<AlbumModel> actorModels = super.toCollectionModel(entities);
     
    actorModels.add(linkTo(methodOn(WebController.class).getAllAlbums()).withSelfRel());
     
    return actorModels;
  }
 
  private List<ActorModel> toActorModel(List<ActorEntity> actors) {
    if (actors.isEmpty())
      return Collections.emptyList();
 
    return actors.stream()
        .map(actor -> ActorModel.builder()
            .id(actor.getId())
            .firstName(actor.getFirstName())
            .lastName(actor.getLastName())
            .build()
            .add(linkTo(
                methodOn(WebController.class)
                .getActorById(actor.getId()))
                .withSelfRel()))
        .collect(Collectors.toList());
  }
}
```

AlbumModel
```
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumModel extends RepresentationModel<AlbumModel> {

albumModel.setId(entity.getId());
    albumModel.setTitle(entity.getTitle());
    albumModel.setDescription(entity.getDescription());
    albumModel.setReleaseDate(entity.getReleaseDate());
    albumModel.setActors(toActorModel(entity.getActors()));

    private Integer id;
    private String title;
    private String description;
    private LocalDateTime releaseDate;
    private List<Address> addresses;

    public AlbumModel(AlbumEntity albumEntity) {
        this.id = albumEntity.getId();
        this.title = albumEntity.getTitle();
        this.description = albumEntity.getDescription();
        this.releaseDate = albumEntity.getReleaseDate();
        this.addresses = albumEntity.getAddresses();
    }
// Getters and setters

}
```
In COntroller :::
WebController.java ::::
```
@RestController
@RequestMapping("/api/person")
@Profile("codebuffer")
@Slf4j
public class WebController {

    private final AlbumService albumService;
    private final PagedResourcesAssembler<AlbumEntity> pagedResourcesAssembler;
    private final AlbumModelAssembler albumModelAssembler;


    public WebController(AlbumService albumService, PagedResourcesAssembler<Person> pagedResourcesAssembler, AlbumModelAssembler albumModelAssembler) {
        this.albumService = albumService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.albumModelAssembler = albumModelAssembler;
    }
    
    @GetMapping("/search")
    public ResponseEntity<org.springframework.hateoas.PagedModel<AlbumModle>> searchAlbum(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") LocalDateTime releaseDate,
            @RequestParam(defaultValue = "5") List<Address> addresses,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        log.info("WebController searchPerson called");
       
        Pageable pageable
                = PageRequest.of(page,size);

        // Fetch the paginated list of Albums
        Page<AlbumEntity> albumPage = albumService.search(title, description, releaseDate, addresses, pageable);

        // // Convert AlbumEntity to AlbumModle
        // Convert AlbumEntity to AlbumModle using AlbumModelAssembler
        org.springframework.hateoas.PagedModel<AlbumModle> pagedModel  = pagedResourcesAssembler
                .toModel(albumPage, AlbumModelAssembler);

        // Convert the Page object into PagedModel
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }
    
    // any pathVariable value would work here in place of {id} we can use {personId} both will work
    @GetMapping("/{id}")
    public Person getAlbumById(@PathVariable("id") Integer id) {
        log.info("WebController getAlbumById() called");
        // Check if Id is a valid and not null
        if (id!=null) {
            return albumService.getAlbumById(id);
        }else throw new IllegalArgumentException("Invalid Id");
    }

    @GetMapping("/all-albums")
    public List<Person> getAllAlbums() {
        log.info("WebController getAllAlbums() called");
        return albumService.getAllAlbums();
    }

}

```

Repository ::::
```
@Repository
@Profile("codebuffer")
public interface AlbumEntityRepository extends MongoRepository<AlbumEntity,Integer> {}
```

main SpringBootApplication :::
```
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO) // Enables Pageable and Sort as method arguments
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL) // Enables HATEOAS support
public class CodeBufferSpringBootMongoDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeBufferSpringBootMongoDbApplication.class, args);
    }
}
```



### Using EntityModel if Model and Entity is the Same Class
Note that if we are using the same class as Model and Entity, then we can skip copying the fields and the ModelAssembler becomes very simple. It is useful if we do not have similar fields in the entity and model and there is no problem in returning the entity representations from the controller.

It helps to avoid copying each field from entity to model class, unnecessarily.

```
@Component
public class AlbumModelAssembler implements RepresentationModelAssembler<Album, EntityModel<Album>> {

  @Override
  public EntityModel<Album> toModel(Album album) {

    return EntityModel.of(album,
      linkTo(methodOn(AlbumController.class).get(album.getId()))
        .withSelfRel());
  }
}
```

IN MY PROJECT :::

In your PersonController, you can use PagedResourcesAssembler to return the paginated results for MongoDB as shown below:

2. Service Layer (Pagination Support)
   In your PersonService, ensure you return a Page<Person> object from your MongoDB query.

3. Repository Layer (Support for Pageable Queries)
   In PersonRepository, ensure the query supports MongoDB's Pageable parameter:

Ensure you convert Person entities to PersonResource (a DTO or a resource class) before passing them to PagedResourcesAssembler.
Use PagedResourcesAssembler to create the PagedModel<PersonResource>.


you'll need to have a PersonResource class that represents the resource you want to return. This class can be a DTO (Data Transfer Object) or a Spring HATEOAS resource.


```
import org.springframework.hateoas.RepresentationModel;

public class PersonResource extends RepresentationModel<PersonResource> {
    private String personId;
    private String firstName;
    private String lastName;
    private Integer age;
    private List<String> hobbies;
    private List<Address> addresses;

    // getters and setters
}

```

pagedResourcesAssembler.toModel(): This method converts the Page<Person> into PagedModel<PersonResource> by applying the transformation 

Define the PersonResourceAssembler
Create an assembler class that extends RepresentationModelAssembler to convert Person into PersonResource.

SAMPLE CODE OF PersonResourceAssembler

```
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PersonResourceAssembler extends RepresentationModelAssemblerSupport<Person, PersonResource> {

    public PersonResourceAssembler() {
        super(PersonController.class, PersonResource.class);
    }

    @Override
    public PersonResource toModel(Person person) {
        PersonResource resource = new PersonResource();
        resource.setPersonId(person.getPersonId().toString());
        resource.setFirstName(person.getFirstName());
        resource.setLastName(person.getLastName());
        resource.setAge(person.getAge());
        resource.setHobbies(person.getHobbies());
        resource.setAddresses(person.getAddresses());
        return resource;
    }
}

```


Modify the Controller
You need to use the PersonResourceAssembler inside your controller. Here's how your controller should look:
```
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService personService;
    private final PagedResourcesAssembler<Person> pagedResourcesAssembler;
    private final PersonResourceAssembler personResourceAssembler;

    public PersonController(PersonService personService,
                            PagedResourcesAssembler<Person> pagedResourcesAssembler,
                            PersonResourceAssembler personResourceAssembler) {
        this.personService = personService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.personResourceAssembler = personResourceAssembler;
    }

    @GetMapping("/search")
    public ResponseEntity<PagedModel<PersonResource>> searchPerson(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        log.info("PersonController searchPerson called");
        log.info("PersonController >> name : {}, minAge : {}, maxAge : {}, city : {}, page : {}, size : {}", name, minAge, maxAge, city, page, size);

        Pageable pageable = PageRequest.of(page, size);

        // Fetch the paginated list of persons
        Page<Person> personPage = personService.search(name, minAge, maxAge, city, pageable);

        // Convert Person to PersonResource using PersonResourceAssembler
        PagedModel<PersonResource> pagedModel = pagedResourcesAssembler.toModel(personPage, personResourceAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }
}
```

PersonResourceAssembler: Converts a Person into a PersonResource, which is then used by the PagedResourcesAssembler to convert the Page<Person> into a PagedModel<PersonResource>.

PagedResourcesAssembler: This class is responsible for converting a Page<Person> into a PagedModel<PersonResource>. It leverages the PersonResourceAssembler to convert each Person entity in the Page into a PersonResource.

Controller: The controller now correctly handles the transformation and returns the paginated resource model as expected.

```
@Configuration
public class PagedResourceAssemblerConfig {

    @Bean
    @ConditionalOnMissingBean(PageableHandlerMethodArgumentResolver.class)
    public PageableHandlerMethodArgumentResolver pageableResolver() {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true);
        return resolver;
    }

    @Bean
    public PagedResourcesAssembler<Object> pagedResourcesAssembler(PageableHandlerMethodArgumentResolver resolver) {
        return new PagedResourcesAssembler<>(resolver, null);
    }
}

```
Use Conditional Bean Definitions
You can also use @ConditionalOnMissingBean to ensure that your custom PagedResourceAssemblerConfig only defines the bean if it's not already present:


================================================================================================
***
***

### Baeldung Tutorial

Spring HATEOAS offers three abstractions for creating the URI – RepresentationModel, Link, and WebMvcLinkBuilder. We can use these to create the metadata and associate it to the resource representation.

the Customer resource without Spring HATEOAS support:
```

 public class Customer {

    private String customerId;
    private String customerName;
    private String companyName;

    // standard getters and setters
}

```
a controller class without Spring HATEOAS support:
```
@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomerDetail(customerId);
    }
}

```
Customer Resource Representation :::
```
{
    "customerId": "10A",
    "customerName": "Jane",
    "customerCompany": "ABC Company"
}

```

### Adding HATEOAS Support
Spring HATEOAS offers three abstractions for creating the URI – RepresentationModel, Link, and WebMvcLinkBuilder. We can use these to create the metadata and associate it to the resource representation.

1. Adding Hypermedia Support to a Resource
   The project provides a base class called RepresentationModel to inherit from when creating a resource representation:
```
public class Customer extends RepresentationModel<Customer> {
    private String customerId;
    private String customerName;
    private String companyName;
 
    // standard getters and setters
}

```
The Customer resource extends from the RepresentationModel class to inherit the add() method. So once we create a link, we can easily set that value to the resource representation without adding any new fields to it.

2. Creating Links
   Spring HATEOAS provides a Link object to store the metadata (location or URI of the resource).

```
{
    "customerId": "10A",
    "customerName": "Jane",
    "customerCompany": "ABC Company",
    "_links":{
        "self":{
            "href":"http://localhost:8080/spring-security-rest/api/customers/10A"
         }
    }
}

```
3. Creating Better Links
   the WebMvcLinkBuilder – which simplifies building URIs by avoiding hard-coded the links.
   The following snippet shows building the customer self-link using the WebMvcLinkBuilder class:
```
linkTo(CustomerController.class).slash(customer.getCustomerId()).withSelfRel();

```
Let’s have a look:
* the linkTo() method inspects the controller class and obtains its root mapping
* the slash() method adds the customerId value as the path variable of the link
* finally, the withSelfMethod() qualifies the relation as a self-link

### Relations
more complex systems may involve other relations as well.
For example, a customer can have a relationship with orders. Let’s model the Order class as a resource as well:
```
public class Order extends RepresentationModel<Order> {
    private String orderId;
    private double price;
    private int quantity;

    // standard getters and setters
}

```
At this point, we can extend the CustomerController with a method that returns all orders of a particular customer:
```
@GetMapping(value = "/{customerId}/orders", produces = { "application/hal+json" })
public CollectionModel<Order> getOrdersForCustomer(@PathVariable final String customerId) {
    List<Order> orders = orderService.getAllOrdersForCustomer(customerId);
    for (final Order order : orders) {
        Link selfLink = linkTo(methodOn(CustomerController.class)
          .getOrderById(customerId, order.getOrderId())).withSelfRel();
        order.add(selfLink);
    }
 
    Link link = linkTo(methodOn(CustomerController.class)
      .getOrdersForCustomer(customerId)).withSelfRel();
    CollectionModel<Order> result = CollectionModel.of(orders, link);
    return result;
}

```

#### Links to Controller Methods
```
Link ordersLink = linkTo(methodOn(CustomerController.class)
  .getOrdersForCustomer(customerId)).withRel("allOrders");

```

#### Spring HATEOAS in Action
the self-link and method link creation all together in a getAllCustomers() method:
```
@GetMapping(produces = { "application/hal+json" })
public CollectionModel<Customer> getAllCustomers() {
    List<Customer> allCustomers = customerService.allCustomers();

    for (Customer customer : allCustomers) {
        String customerId = customer.getCustomerId();
        Link selfLink = linkTo(CustomerController.class).slash(customerId).withSelfRel();
        customer.add(selfLink);
        if (orderService.getAllOrdersForCustomer(customerId).size() > 0) {
            Link ordersLink = linkTo(methodOn(CustomerController.class)
              .getOrdersForCustomer(customerId)).withRel("allOrders");
            customer.add(ordersLink);
        }
    }

    Link link = linkTo(CustomerController.class).withSelfRel();
    CollectionModel<Customer> result = CollectionModel.of(allCustomers, link);
    return result;
}
```

****



Note: Page starts at 0 so be careful. So if there are 1000 objects and we give a page of 2 and size of 50. We will get the third set of 101-150 objects.

pagination at repository level so to get Page<Customer> as return type. so pageable we do not do at controller level or service level that means we fetch all records from database then apply pagination is not good practice instead we apply pagination at repository level and supply pageable object to the repository method and in return we get Page<Customer> type as return type.

@Query(FILTER_CUSTOMERS_ON_FIRST_NAME_AND_LAST_NAME_QUERY)
Page<Customer> findByFirstNameLikeAndLastNameLike(String firstNameFilter, String lastNameFilter, Pageable pageable);





to use hateoas we convert the page format to a hateoas PagedModel format. To do so we need 2 additional files

Customer Model (extends RepresentationModel<>)
Customer Model Assembler(extends RepresentationModelAssemblerSupport<,>)



![](D:\jlab\git2024\external-api-call\b616b9e913c34d468a34261cb32746f6.png)

Taking a closer look at our output we notice some really beneficial information.

“_links” - this key contains an object with links to the first,self,next and last page. These links can be used from the frontend team to use pagination in the UI.
“page” - this tells us how many elements, totaly elements after filtering and sorting, total pages and current page number.
“_embedded” - contains the list of CustomerModels. The actual data that is converted from the entity to the model.








































