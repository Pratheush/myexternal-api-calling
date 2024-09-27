package com.mylearning.journalapp.service;

import com.mylearning.journalapp.entity.JournalEntry;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.exception.JournalEntryAddingFailedException;
import com.mylearning.journalapp.exception.JournalEntryNotFoundException;
import com.mylearning.journalapp.exception.UserNotFoundException;
import com.mylearning.journalapp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
//@Profile("atlas")
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;

    private final UserService userService;

    private final String USER_NOT_FOUND = "User Not Found By : %s";

    public JournalEntryService(JournalEntryRepository journalEntryRepository, UserService userService) {
        this.journalEntryRepository = journalEntryRepository;
        this.userService = userService;
    }

    @Transactional
    public JournalEntry addJournal(JournalEntry entry, String userName) {
        log.info("JournalEntryService addJournal() called entry : {}, userName : {}", entry, userName);
        try {
            User savedUser = userService.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND,userName)));
            entry.setCreatedOn(LocalDateTime.now());
            JournalEntry savedEntry = journalEntryRepository.save(entry);
            savedUser.getJournalEntries().add(savedEntry);

            //due to this line of code statement journal entry was setting each time new journal is added and old journal entries are removed from the used since we are setting it here
            //savedUser.setJournalEntries(List.of(savedEntry));

            // simulate acid property through @Transactional by setting username null since userName cannot be null
            //savedUser.setUserName(null);

            userService.saveUser(savedUser);
            log.info("JournalEntryService addJournal() called entry : {}, userName : {}", entry, userName);
            return savedEntry;
        } catch (RuntimeException e) {
            throw new JournalEntryAddingFailedException("An error occurred while adding journal entry.  "+e.getMessage());
        }
    }

    public List<JournalEntry> getAllJournalEntriesByUserName(String userName) {
        User user = userService.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND,userName)));
        return user.getJournalEntries();
    }

    public List<JournalEntry> getJournalEntryUsingTitleByUsername(String userName, String titleName) {
        User user = userService.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND,userName)));
        List<JournalEntry> entriesBySimilarTitle = journalEntryRepository.findBySimilarTitle(titleName).orElseThrow(() -> new JournalEntryNotFoundException(String.format("Journal Entry Not Found With Title : %s",titleName)));
        if(!entriesBySimilarTitle.isEmpty()){
            return user.getJournalEntries().stream().filter(userJournalEntry -> userJournalEntry.getTitle().equals(titleName)).toList();
        }
        else throw new JournalEntryNotFoundException(String.format("Journal Entry with Title : %s Not Found For User : %s",titleName,userName));
    }

    @Transactional
    public JournalEntry updateJournalEntryUsingTitleByUsername(JournalEntry entry, String userName) {
        AtomicReference<JournalEntry> atomicJournalEntry = new AtomicReference<>();
        User user = userService.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND,userName)));
        List<JournalEntry> journalEntryList = user.getJournalEntries().stream().filter(journalEntry -> journalEntry.getTitle().equals(entry.getTitle())).toList();
        if(!journalEntryList.isEmpty()){
            Optional<JournalEntry> entryByTitle = journalEntryRepository.findByTitle(entry.getTitle());
            entryByTitle.ifPresentOrElse(entryPresent -> {
                entryPresent.setContent(!entry.getContent().isEmpty() && !entry.getContent().isBlank() ? entry.getContent() : entryPresent.getContent());
                entryPresent.setUpdatedOn(LocalDateTime.now());
                atomicJournalEntry.set(journalEntryRepository.save(entryPresent));
            },() ->{
                throw new JournalEntryNotFoundException(String.format("Journal Entry Not Found with Title : %s",entry.getTitle()));
            });
        }else {
            throw new JournalEntryNotFoundException(String.format("Journal Entry List has No Journal Entry with Title : %s",entry.getTitle()));
        }
        return atomicJournalEntry.get();
    }

    @Transactional
    public String deleteJournalEntryUsingTitleByUserName(String titleName,String userName) {

       User user = userService.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND,userName)));
        List<JournalEntry> userJournalEntryList = user.getJournalEntries().stream().filter(entry -> entry.getTitle().equals(titleName)).toList();

        // below code statement did not work as expected did not check for matching titleName present in the list and always gave true
        //boolean matchedEntries = userJournalEntryList.stream().map(JournalEntry::getTitle).allMatch(entryTitle -> entryTitle.equals(titleName));

        // below code statement did not work as expected did not check for matching titleName present in the list and always gave true
        //boolean matchedEntries = userJournalEntryList.stream().allMatch(entry -> entry.getTitle().equals(titleName));

        if(userJournalEntryList.isEmpty()) throw new JournalEntryNotFoundException(String.format("Journal Entry with Title : %s does not exist for User : %s",titleName,userName));
        journalEntryRepository.findBySimilarTitle(titleName).ifPresentOrElse(entryList -> {
            if(new HashSet<>(entryList).containsAll(userJournalEntryList)) {
                user.getJournalEntries().removeAll(userJournalEntryList);
                journalEntryRepository.deleteAll(entryList);
                userService.saveUser(user);
            }
        },() -> {
            throw new JournalEntryNotFoundException(String.format("Journal Not Found with Title : %s for User : %s",titleName,userName));
        });

        /*AtomicBoolean removed= new AtomicBoolean(false);
        journalEntryRepository.findBySimilarTitle(titleName).ifPresentOrElse((entries) ->{
                journalEntryRepository.deleteAll(entries);
                userService.findByUserName(userName).ifPresentOrElse(user -> {
                    List<JournalEntry> journalEntries = user.getJournalEntries();
                    removed.set(journalEntries.removeIf(journalEntry -> journalEntry.getTitle().equals(titleName)));
                    if(removed.get()){
                        userService.saveUser(user);
                    }
                },() -> {
                    throw new UserNotFoundException(STR."User Not Found ::\{userName}");
                });
        },() -> {
            throw new JournalEntryNotFoundException(STR."Journal Not Found with Title : \{titleName}");
        });*/
        return "Journal Entry deleted";
    }
}
