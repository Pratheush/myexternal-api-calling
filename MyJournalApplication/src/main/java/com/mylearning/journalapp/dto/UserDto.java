package com.mylearning.journalapp.dto;

import com.mylearning.journalapp.entity.JournalEntry;
import lombok.Builder;
import org.bson.types.ObjectId;

import java.util.List;

@Builder
public record UserDto(ObjectId id, String userName, String email,boolean sentimentAnalysis, String password, List<JournalEntry> journalEntries,List<String> roles) {
}
