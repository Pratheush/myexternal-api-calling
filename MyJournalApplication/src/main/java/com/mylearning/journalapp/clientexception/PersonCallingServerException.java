package com.mylearning.journalapp.clientexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PersonCallingServerException extends RuntimeException {
    public PersonCallingServerException(String message) {super(message);}
}
