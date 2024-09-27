package com.mylearning.journalapp.repository;

import com.mylearning.journalapp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Profile("atlas")
@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {

    /**
     * this kind of method we call Query method DSL
     * @param title
     * @return
     */
    Optional<JournalEntry> findByTitle(String title);

    /**
     *
     * $regex: ?0: This indicates that the first method parameter will be used as the regex pattern.
     * $options: 'i': This makes the search case-insensitive.
     *
     * @Query("{ 'title': { $regex: ?0, $options: 'i' } }"): This MongoDB query uses a regex pattern to
     * find documents where the title field matches the given pattern. The $options: 'i' makes the search case-insensitive.
     * @param titlePattern
     * @return
     *
     *Starts with a specific substring (^):
     * @Query("{ 'title': { $regex: '^?0', $options: 'i' } }")
     * Example: findByTitleContaining("jour")
     * Matches titles that start with "jour":
     * "Journal Entry"
     * "Journey into Coding"
     * Does not match:
     * "My Journal"
     * "Tech Journal"
     *
     * ----------------------------------------------------------------------
     *
     * Ends with a specific substring ($):
     * @Query("{ 'title': { $regex: '?0$', $options: 'i' } }")
     * Example: findByTitleContaining("entry")
     * Matches titles that end with "entry":
     * "New Entry"
     * "Journal Entry
     * Does not match:
     * "Entry Journal"
     * "Another entryway"
     *
     * ----------------------------------------------------------------------
     *
     * Wildcard match (.*):
     * @Query("{ 'title': { $regex: '.*?0.*', $options: 'i' } }")
     * Example: findByTitleContaining("test")
     * Matches any title containing the sequence "test" at any position (same as the first example), but the .* makes it explicit:
     * "Testing 101"
     * "Unit Test"
     * "Best Test Practices"
     *
     * --------------------------------------------------------------------------
     *
     * Match exact word (\b):
     * @Query("{ 'title': { $regex: '\\b?0\\b', $options: 'i' } }")
     * Example: findByTitleContaining("journal")
     * Matches titles where "journal" is a separate word, so it won’t match part of another word:
     * "My Journal"
     * Does not match:
     * "Best Journalists"
     *
     * --------------------------------------------------------------------------------
     *
     * Alternation (OR) (|):
     * @Query("{ 'title': { $regex: '?0|?1', $options: 'i' } }")
     * Example: findByTitleContaining("journal", "blog")
     * Matches titles containing either "journal" or "blog":
     * "My Journal"
     * "Tech Blog"
     *
     * --------------------------------------------------------------------------------------
     *
     * Optional characters (?):
     * @Query("{ 'title': { $regex: 'colou?r', $options: 'i' } }")
     * Example: findByTitleContaining("color")
     * Matches titles with optional characters (e.g., matches both "color" and "colour"):
     * "Colorful Days"
     * "Colour Theory"
     *
     * ====================================================================================
     *
     * More Complex Regular Expression Usage
     * Matching titles that start with "Tech" and contain the word "Spring":
     * @Query("{ 'title': { $regex: '^Tech.*Spring.*', $options: 'i' } }")
     * his will match titles like:
     * "Tech Trends in Spring"
     * "Technology and Spring Boot"
     * Explanation: The pattern ^Tech.*Spring.* looks for titles that start with "Tech" (^Tech),
     * and after that, it can have any characters (.*), and then "Spring".
     *
     * ----------------------------------------------------------------------------
     *
     * Matching titles containing either "Java" or "Kotlin":
     * @Query("{ 'title': { $regex: 'Java|Kotlin', $options: 'i' } }")
     * This will match titles like:
     * "Java for Beginners"
     * "Advanced Kotlin Programming"
     * Explanation: The pattern Java|Kotlin uses the alternation operator (|), so it matches either "Java" or "Kotlin".
     *
     * MongoDB's $regex supports a wide range of options and patterns for complex searches.
     *
     *
     *
     */
    // Custom query to fetch entries with similar titles using regex
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Optional<List<JournalEntry>> findBySimilarTitle(String titlePattern);
}
