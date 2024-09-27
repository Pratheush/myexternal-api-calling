package com.mylearning.journalapp.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

@TestConfiguration
@Testcontainers
@Slf4j
public class TestContainerConfig {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.7"));
    //.withEnv("MONGO_REPLICA_SET", "rs0");
    //.withCommand("--replSet rs0 --sslMode disabled")
    //.withExposedPorts(27017)
    //.waitingFor(Wait.forListeningPort());
    //.withCommand("--replSet rs0 --sslMode disabled");



    // =mongodb://localhost:27017/test?connectTimeoutMS=10000&socketTimeoutMS=30000
    // URI ::: mongodb://localhost:64871/test
    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",() -> mongoDBContainer.getReplicaSetUrl());
        registry.add("spring.data.mongodb.database",() -> "test");
        log.info("URI ::: {}", mongoDBContainer.getReplicaSetUrl());
    }

    @BeforeAll
    static void initContainer() {
        mongoDBContainer.start();
    }
    @AfterAll
    static void tearDownContainer(){
        mongoDBContainer.stop();
    }

   /* @Bean
    public MongoDBContainer mongoDBContainer() {
        MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.7");
        mongoDBContainer.start();
        return mongoDBContainer;
    }*/

    /*@Bean
    public MongoTemplate mongoTemplate(MongoDBContainer mongoDBContainer) {
        String connectionString = mongoDBContainer.getReplicaSetUrl();
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(connectionString + "?socketTimeoutMS=30000"));
    }*/

    /*@Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(10, TimeUnit.SECONDS)  // Connection timeout (10 seconds)
                        .readTimeout(30, TimeUnit.SECONDS))    // Read timeout (30 seconds)
                .build();
    }*/

    /*@Bean
    public MongoTemplate mongoTemplate() {
        //var mongoClient = MongoClients.create(mongoClientSettings());
        return new MongoTemplate(mongoClient(), "test");
    }

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToSocketSettings(builder -> builder
                        .readTimeout(60, TimeUnit.SECONDS))  // Read timeout of 30 seconds
                .applyToClusterSettings(builder -> builder
                        .serverSelectionTimeout(60, TimeUnit.SECONDS))  // Connection timeout of 10 seconds
                .build();

        return MongoClients.create(settings);
    }*/
}
