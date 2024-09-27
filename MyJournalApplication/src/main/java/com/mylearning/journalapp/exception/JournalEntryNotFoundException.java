package com.mylearning.journalapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class JournalEntryNotFoundException extends RuntimeException{
    public JournalEntryNotFoundException(String message){
        super(message);
    }
}
