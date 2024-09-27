package org.dailycodebuffer.codebufferspringbootmongodb.converter;


import org.bson.Document;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Student;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentConverter {

    private MongoTemplate mongoTemplate;

    public DocumentConverter(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public Document convertStudentToDocument(Student student) {
        // Get the MongoConverter from MongoTemplate
        MongoConverter converter = mongoTemplate.getConverter();

        // Convert Student object to Document
        Document document = (Document) converter.convertToMongoType(student);

        return document;
    }

    public List<Document> convertStudentsToDocuments(List<Student> students) {
        // Get the MongoConverter from MongoTemplate
        MongoConverter converter = mongoTemplate.getConverter();

        // Convert each Student object to Document using stream and collect
        List<Document> documents = students.stream()
                .map(student -> (Document)convertStudentToDocument(student))
                .collect(Collectors.toList());

        return documents;
    }
}
