package com.mylearning.journalapp.entity;

import com.mylearning.journalapp.enums.Sentiment;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "journal_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {
    @Id
    private ObjectId id;
    @Field("title")
    @Indexed(unique = true)
    @NonNull
    private String title;
    private String content;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Sentiment sentiment;
}
