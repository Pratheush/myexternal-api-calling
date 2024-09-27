package com.mylearning.journalapp.clientexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonCallingClientException extends RuntimeException {
    public PersonCallingClientException(String message) {super(message);}
}
