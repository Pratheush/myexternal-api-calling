package com.mylearning.journalapp.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class PersonJsonProcessingException extends JsonProcessingException {

    protected PersonJsonProcessingException(String msg) {
        super(msg);
    }
    public static PersonJsonProcessingException personException(String msg){
        return new PersonJsonProcessingException(msg);
    }
}
