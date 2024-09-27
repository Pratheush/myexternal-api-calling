package org.dailycodebuffer.codebufferspringbootmongodb.repository;

import org.dailycodebuffer.codebufferspringbootmongodb.collection.Photo;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("codebuffer")
public interface PhotoRepository extends MongoRepository<Photo, String> {
}
