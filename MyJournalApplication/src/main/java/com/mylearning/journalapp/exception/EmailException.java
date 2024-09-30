package com.mylearning.journalapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class EmailException extends RuntimeException{
    public EmailException(String message){super(message);}
}
