package com.mylearning.journalapp.dto;

import lombok.Builder;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Builder
public record JournalEntryDto(ObjectId id, String title, String content, LocalDateTime createdOn, LocalDateTime updatedOn) {
}
