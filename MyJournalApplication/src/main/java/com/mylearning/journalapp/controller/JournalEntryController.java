package com.mylearning.journalapp.controller;

import com.mylearning.journalapp.dto.JournalEntryDto;
import com.mylearning.journalapp.entity.JournalEntry;
import com.mylearning.journalapp.service.JournalEntryService;
import jakarta.websocket.server.PathParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/journal")
//@Profile("atlas")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    public JournalEntryController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @PostMapping
    public ResponseEntity<?> addJournal(@RequestBody @NonNull JournalEntryDto entryDto) {
        JournalEntry entry = JournalEntry.builder()
                .title(entryDto.title())
                .content(entryDto.content())
                .sentiment(entryDto.sentiment())
                .build();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            Authentication authentication = securityContext.getAuthentication();
            String userName = authentication.getName();
            log.info("JournalEntryController addJournal() called entry : {}, userName : {}", entry, userName);
            JournalEntry journalEntry = journalEntryService.addJournal(entry, userName);

            JournalEntryDto.builder()
                    .id(journalEntry.getId())
                    .title(journalEntry.getTitle())
                    .content(journalEntry.getContent())
                    .createdOn(journalEntry.getCreatedOn())
                    .sentiment(journalEntry.getSentiment())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(journalEntry);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesByUserName(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = authentication.getName();
        List<JournalEntry> allJournalEntriesByUserName = journalEntryService.getAllJournalEntriesByUserName(userName);

        List<JournalEntryDto> journalEntryDtos = allJournalEntriesByUserName.stream()
                .map(journalEntry -> JournalEntryDto.builder()
                        .id(journalEntry.getId())
                        .title(journalEntry.getTitle())
                        .content(journalEntry.getContent())
                        .createdOn(journalEntry.getCreatedOn())
                        .updatedOn(journalEntry.getUpdatedOn())
                        .sentiment(journalEntry.getSentiment())
                        .build())
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(journalEntryDtos);
    }

    @GetMapping("/title")
    public ResponseEntity<?> getJournalEntryUsingTitleByUsername(@RequestParam(value = "title",required = true) String titleName){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = authentication.getName();
        List<JournalEntry> journalEntriesList = journalEntryService.getJournalEntryUsingTitleByUsername(userName, titleName);
        List<JournalEntryDto> journalEntryDtos = journalEntriesList.stream()
                .map(journalEntry -> JournalEntryDto.builder()
                        .id(journalEntry.getId())
                        .title(journalEntry.getTitle())
                        .content(journalEntry.getContent())
                        .createdOn(journalEntry.getCreatedOn())
                        .updatedOn(journalEntry.getUpdatedOn())
                        .sentiment(journalEntry.getSentiment())
                        .build())
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(journalEntryDtos);
    }

    @PutMapping
    public ResponseEntity<?> updateJournalEntryUsingTitleByUsername(@RequestBody JournalEntryDto entryDto){
        JournalEntry entry = JournalEntry.builder()
                .title(entryDto.title())
                .content(entryDto.content())
                .sentiment(entryDto.sentiment())
                .build();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = authentication.getName();
        JournalEntry updatedJournalEntry = journalEntryService.updateJournalEntryUsingTitleByUsername(entry, userName);

        JournalEntryDto journalEntryDto = JournalEntryDto.builder()
                .id(updatedJournalEntry.getId())
                .title(updatedJournalEntry.getTitle())
                .content(updatedJournalEntry.getContent())
                .createdOn(updatedJournalEntry.getCreatedOn())
                .updatedOn(updatedJournalEntry.getUpdatedOn())
                .sentiment(updatedJournalEntry.getSentiment())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(journalEntryDto);
    }

    @DeleteMapping("/title")
    public ResponseEntity<String> deleteJournalEntryUsingTitleByUserName(@RequestParam(value = "title",required = true) String titleName){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = authentication.getName();
        String msg = journalEntryService.deleteJournalEntryUsingTitleByUserName(titleName,userName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(msg);
    }

}
