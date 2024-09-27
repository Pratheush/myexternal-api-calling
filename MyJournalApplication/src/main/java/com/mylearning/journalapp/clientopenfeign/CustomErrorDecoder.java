package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.clientexception.PersonNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()){
            case 400:
                return new PersonCallingClientException("Client Side Error");
            case 404:
                return new PersonNotFoundException("Person Not Found");
            default:
                return new Exception("Generic error");
        }
    }
}
