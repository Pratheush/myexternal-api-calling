package com.mylearning.journalapp.repository;

import com.mylearning.journalapp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Profile("atlas")
@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByUserName(String userName);
    void deleteByUserName(String userName);
}
