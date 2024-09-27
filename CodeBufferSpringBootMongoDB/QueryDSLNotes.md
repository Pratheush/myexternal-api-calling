## Using QueryDSL in a Spring Boot Application
QueryDSL is a powerful Java-based query framework that allows developers to write type-safe queries against various data sources, including SQL databases, MongoDB, and Elasticsearch. It provides a fluent API for building complex queries in a concise and readable way, making it a popular choice for developers working with complex data models.

When used in conjunction with Spring Boot, QueryDSL can simplify the process of writing database queries by providing a type-safe and fluent syntax for building queries. It can also help to reduce the amount of boilerplate code needed to perform common database operations, such as filtering and sorting data.

start by setting up a new Spring Boot project, creating a JPA entity, and generating Q classes using the QueryDSL annotation processor. We'll then write a QueryDSL query to retrieve data from the database,

### Step 1: Set up a Spring Boot Project
1. include the following dependencies in your project’s pom.xml file:
#### For JPA QueryDSL
```
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <!--<classifier>jakarta</classifier>-->
    <version>5.0.0</version>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>5.0.0</version>
    <scope>provided</scope>
</dependency>
```

#### For MONGODB QueryDSL
```
<!-- https://mvnrepository.com/artifact/com.querydsl/querydsl-mongodb -->
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-mongodb</artifactId>
            <version>5.0.0</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.querydsl/querydsl-apt -->
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-apt</artifactId>
            <version>5.0.0</version>
            <!--<classifier>jakarta</classifier>-->
            <scope>provided</scope>
        </dependency>
```
These dependencies will allow you to use QueryDSL in your Spring Boot application.

After Adding Dependencies Add Configuration if you are adding the Configuration check is it working otherwise don't do the Configuration

2. Create a `QuerydslConfiguration` class. This class will be responsible for configuring Querydsl.
```
@Configuration
public class QuerydslConfiguration {

@Bean
public QuerydslJpaQueryFactory queryFactory(EntityManager entityManager) {
return new QuerydslJpaQueryFactory(entityManager);
}
}
```

### Step 2: Create a JPA Entity
A simple JPA entity

```
import jakarta.persistence.*;

@Entity
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    
    private String lastName;
    
    // getters and setters omitted for brevity
    
}
```

Create a `QuerydslJpaRepository` interface. This interface extends the JPA `JpaRepository` interface and adds a number of Querydsl methods.
```
java
public interface UserRepository extends JpaRepository, QuerydslJpaRepository {

@Query(“SELECT u FROM User u WHERE u.name = :name”)
User findByName(@Param(“name”) String name);

}
```
use Querydsl with MongoDB is to use the `QuerydslMongoRepository` interface. This interface extends the `MongoRepository` interface and adds a number of methods for building queries.



### Step 3: Generate Q Classes
QueryDSL uses code generation to create Q classes that correspond to your JPA entities. These classes allow you to write type-safe queries against your database. To generate Q classes for your Person entity, add the following plugin to your project's pom.xml file:
APT means Annotation Processing Tool

#### For JPA QueryDSL add this apt-maven-plugin
```
<build>
    <plugins>
        <plugin>
            <groupId>com.mysema.maven</groupId>
            <artifactId>apt-maven-plugin</artifactId>
            <version>1.1.3</version>
            <executions>
                <execution>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>process</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>target/generated-sources/java</outputDirectory>
                        <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
#### Another way of Writing For JPA QueryDSL add this apt-maven-plugin
```
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.mysema.query.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>

```

#### For JPA QueryDSL add this apt-maven-plugin
```
<!-- Add plugin for Mongo Query DSL -->
<build>
    <plugins>
            <plugin>
                <groupId>com.mysema.maven</groupId>
                <artifactId>apt-maven-plugin</artifactId>
                <version>1.1.3</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <configuration>
                            <includes>
                                <include> org.dailycodebuffer.codebufferspringbootmongodb.collection.** </include>
                            </includes>
                            <outputDirectory>target/generated-sources/java</outputDirectory>
                            <processor>
                                org.springframework.data.mongodb.repository.support.MongoAnnotationProcessor
                            </processor>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
         </plugins>
</build>
```

This plugin will generate Q classes for all of your JPA entities in the target/generated-sources/java directory. Run the following Maven command to generate the Q classes:
To Generate Q CLasses ::
mvn clean compile

### Step 4: Write a QueryDSL query
Now that we have a Q class for our Person entity, we can write a QueryDSL query against our database. In this example, we'll use QueryDSL to find all people whose last name is "Smith".

```
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

import java.util.List;

@Repository
public class PersonRepository {

    @Autowired
    private EntityManager entityManager;

    public List<Person> findPeopleByLastName(String lastName) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QPerson person = QPerson.person;
        return queryFactory.selectFrom(person)
                .where(person.lastName.eq(lastName))
                .fetch();
    }
}
```

The JPAQueryFactory is used to create a QueryDSL query against the Person entity, and the QPerson class is used to reference the Person entity in a type-safe manner. The where method is used to specify the condition for the query, and the fetch method is used to execute the query and return the results.

### Step 5: Test the QueryDSL Query
```
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testFindPeopleByLastName() {
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Smith");
        entityManager.persist(person1);

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Doe");
        entityManager.persist(person2);

        List<Person> result = personRepository.findPeopleByLastName("Smith");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(0).getLastName()).isEqualTo("Smith");
    }
}
```

### Step 6: Run the Spring Boot Application
mvn spring-boot:run -Dspring.profile.active=test

***
***

### Build Query Using JPAQuery
To build a query, first, we’ll need an instance of a JPAQueryFactory, which is a preferred way of starting the building process. The only thing that JPAQueryFactory needs is an EntityManager, which should already be available in your JPA application via EntityManagerFactory.createEntityManager() call or @PersistenceContext injection.

```
EntityManagerFactory emf = 
  Persistence.createEntityManagerFactory("com.mylearning.querydsl.intro");
EntityManager em = entityManagerFactory.createEntityManager();
JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);

QUser user = QUser.user;

User c = queryFactory.selectFrom(user)
  .where(user.login.eq("David"))
  .fetchOne();

```

The fetchOne() method returns null if the object can’t be found, but throws a NonUniqueResultException if there are multiple entities satisfying the .where() condition.

First – QPerson has a default instance variable which can be accessed as a static field:
```
QPerson person = QPerson.person;
```
Alternatively, you can define your own Person variables like this:
```
QPerson person = new QPerson("Erich", "Gamma");
```
use JPAQuery instances for our queries, the entityManager is a JPA EntityManager.
```
JPAQuery query = new JPAQuery(entityManager);
```

1. retrieve all the persons with the first name “Kent”
```
QPerson person = QPerson.person;
JPAQuery query = new JPAQuery(entityManager);
List<Person> persons = query.from(person).where(person.firstName.eq("Kent")).list(person);
```
The from call defines the query source and projection, and the where part defines the filter and list tells Querydsl to return all matched elements.

2. use multiple filters:
```
query.from(person).where(person.firstName.eq("Kent"), person.surname.eq("Beck"));
```
OR
```
query.from(person).where(person.firstName.eq("Kent").and(person.surname.eq("Beck")));
```

In native JPQL form, the query would be written like this:
```
select person from Person as person where person.firstName = "Kent" and person.surname = "Beck"
```
If you want to combine the filters via “or” then use the following pattern:
```
query.from(person).where(person.firstName.eq("Kent").or(person.surname.eq("Beck")));
```

3. Ordering in Querydsl 
ordering our results in descending order by the surname field
```
QPerson person = QPerson.person;
JPAQuery query = new JPAQuery(entityManager);
List<Person> persons = query.from(person)
    .where(person.firstname.eq(firstname))
    .orderBy(person.surname.desc())
    .list(person);
```
4. Aggregation in Querydsl
 aggregation like Sum, Avg, Max, Min
```
QPerson person = QPerson.person;  
JPAQuery query = new JPAQuery(entityManager);  
int maxAge = query.from(person).list(person.age.max()).get(0);
```
5. Aggregation With GroupBy
The com.mysema.query.group.GroupBy class provides aggregation functionality which we can use to aggregate query results in memory.

the result is returned as Map with firstname as the key and max age as the value:

```
QPerson person = QPerson.person;   
Map<String, Integer> results = 
  query.from(person).transform(
      GroupBy.groupBy(person.firstname).as(GroupBy.max(person.age)));
```


6. to group all posts by title and count duplicating titles. 
```
NumberPath<Long> count = Expressions.numberPath(Long.class, "c");

List<Tuple> userTitleCounts = queryFactory.select(
  blogPost.title, blogPost.id.count().as(count))
  .from(blogPost)
  .groupBy(blogPost.title)
  .orderBy(count.desc())
  .fetch();
```
We selected the blog post title and count of duplicates, grouping by title and then ordering by aggregated count. Notice we first created an alias for the count() field in the .select() clause, because we needed to reference it in the .orderBy() clause.

7. Complex Queries With Joins and Subqueries
find all users that wrote a post titled “Hello World!”
```
QBlogPost blogPost = QBlogPost.blogPost;

List<User> users = queryFactory.selectFrom(user)
  .innerJoin(user.blogPosts, blogPost)
  .on(blogPost.title.eq("Hello World!"))
  .fetch();
```
Now let’s try to achieve the same with a subquery:
```
List<User> users = queryFactory.selectFrom(user)
  .where(user.id.in(
    JPAExpressions.select(blogPost.user.id)
      .from(blogPost)
      .where(blogPost.title.eq("Hello World!"))))
  .fetch();
```

8. Modifying Data
JPAQueryFactory allows not only constructing queries but also modifying and deleting records.
```
queryFactory.update(user)
  .where(user.login.eq("Ash"))
  .set(user.login, "Ash2")
  .set(user.disabled, true)
  .execute();
```
We can have any number of .set() clauses we want for different fields. The .where() clause is not necessary, so we can update all the records at once.

To delete the records matching a certain condition
```
queryFactory.delete(user)
  .where(user.login.eq("David"))
  .execute();
```
The .where() clause is also not necessary, but be careful, because omitting the .where() clause results in deleting all of the entities of a certain type.

JPAQueryFactory doesn’t have the .insert() method. This is a limitation of the JPA Query interface. The underlying jakarta.persistence.Query.executeUpdate() method is capable of executing update and delete but not insert statements. To insert data, you should simply persist the entities with EntityManager.


***
***


Criteria queries: Querydsl provides a criteria API that allows you to build queries using a declarative syntax.
Native queries: Querydsl allows you to write native queries using the underlying database’s SQL dialect.


#### Querydsl with MongoDB
Using Querydsl with MongoDB repositories
Querydsl MongoDB criteria queries
Querydsl MongoDB native queries

use Querydsl with MongoDB is to use the `QuerydslMongoRepository` interface. This interface extends the `MongoRepository` interface and adds a number of methods for building queries.

1. the following code uses the `findByFirstName()` method to find all documents in the `users` collection that have the first name `”John”`:
```
List users = querydslMongoRepository.findByFirstName(“John”);
```
2. the following code uses the `where()` method to build a criteria query that matches documents in the `users` collection that have the first name `”John”` and the last name `”Doe”`:
```
Criteria criteria = Criteria.where(“firstName”).is(“John”).and(“lastName”).is(“Doe”);
List users = querydslMongoRepository.findAll(criteria);
```
`com.querydsl.mongodb.core.query.Criteria` class. The `Criteria` class provides a number of methods for building criteria queries.

3. the following code uses the `where()` method to build a criteria query that matches documents in the `users` collection that have a `salary` property that is greater than or equal to $100,000:
```
Criteria criteria = Criteria.where(“salary”).gte(100000);
```

Querydsl also supports native queries. Native queries are queries that are written in the MongoDB query language. You can use native queries to perform any type of query that is supported by the MongoDB query language.

To use a native query, you can use the `QuerydslMongoQuery` class. 

the following code uses the `QuerydslMongoQuery` class to build a native query that matches documents in the `users` collection that have the first name `”John”`:
```
QuerydslMongoQuery query = new QuerydslMongoQuery(mongoTemplate);
query.addCriteria(Criteria.where(“firstName”).is(“John”));
List users = query.execute(User.class);
```

#### Querydsl with Elasticsearch
Using Querydsl with Elasticsearch repositories
Querydsl Elasticsearch criteria queries
Querydsl Elasticsearch native queries

use Querydsl with Elasticsearch is to use the `QuerydslElasticsearchRepository` interface.

the following code uses the `findByFirstName()` method to find all documents in the `users` index that have the first name `”John”`:
```
List users = querydslElasticsearchRepository.findByFirstName(“John”);
```

#### Q: Why should I use Querydsl with Spring Boot?
There are several reasons why you should use Querydsl with Spring Boot:
1. Performance: Querydsl can significantly improve the performance of your queries. This is because Querydsl uses a compile-time approach to generate optimized SQL queries.
2. Code quality: Querydsl can help you to write better code. This is because Querydsl provides a type-safe API that makes it easier to avoid errors.
3. Reusability: Querydsl queries can be reused in multiple applications and projects. This can save you time and effort.


#### Q: How do I get started with Querydsl and Spring Boot?
A: To get started with Querydsl and Spring Boot, you can follow these steps:
1. Add the Querydsl dependency to your Spring Boot project.
2. Create a `QuerydslConfig` class to configure Querydsl.
3. Use the Querydsl API to build your queries.

#### Q: What are some of the common problems with Querydsl?
A: There are a few common problems that you may encounter when using Querydsl:
1. Performance: If you are not careful, Querydsl queries can actually slow down your application. This is because Querydsl uses a compile-time approach to generate optimized SQL queries. If your queries are not complex, you may be better off using the standard JDBC API.
2. Code complexity: Querydsl queries can be more complex than standard SQL queries. This is because Querydsl provides a lot of features that can make your queries more powerful. If you are not familiar with Querydsl, you may find it difficult to write complex queries.
3. Reusability: Querydsl queries are not as reusable as standard SQL queries. This is because Querydsl queries are specific to the database schema. If you change your database schema, you will need to rewrite your queries.


#### Q: How can I avoid the common problems with Querydsl?
Use Querydsl only when necessary: If your queries are not complex, you may be better off using the standard JDBC API.
Keep your queries simple: Querydsl queries can be more complex than standard SQL queries. If you are not familiar with Querydsl, you may find it difficult to write complex queries.



