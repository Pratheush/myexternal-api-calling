package org.dailycodebuffer.codebufferspringbootmongodb.myjwt;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByUserName(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByUserNameOrEmail(String username, String email);

    Boolean existsByUserName(String username);
}
