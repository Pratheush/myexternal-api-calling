package org.dailycodebuffer.codebufferspringbootmongodb.myjwt;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private ObjectId id;
    private String name;
    // Due to Indexing our searching will be fast on userName in Database
    // we have to set property for index creation automatically by spring i.e. spring.data.mongodb.auto-index-creation=true
    // this annotation processor will check for Null Check
    @Field("userName")
    @Indexed(unique = true)  // Ensure that the username is unique
    @NonNull
    private String userName;
    @Indexed(unique = true)
    private String email;
    @NonNull
    private String password;

    // creating a reference of JournalEntry inside User thus creating a relationship between two collection users and journal_entries
    @DBRef
    private List<Role> roles;
}
