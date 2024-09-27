package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientresponse.Person;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @RequestLine(value = "GET /{name}/{age}")
 * Person getPersonByNameAndAgePathVariableExchange(@Param(value = "name") @PathVariable String name, @Param(value = "age") @PathVariable Integer age);
 *
 * @RequestLine Feign uses its own @RequestLine to define the HTTP method and path. In this case, the GET method with path variables {name} and {age} is specified as GET /{name}/{age}.
 * @Param Annotation : Feign uses the @Param annotation to bind method parameters to path variables or query parameters in the URL. You need to specify the same variable names as those used in the @RequestLine path (i.e., {name} and {age}).
 * Remove Spring's @PathVariable: The Feign client doesn't use Spring annotations like @PathVariable. Instead, it relies on Feign's @Param.
 *
 */

public interface PersonFeignClientByNameAGe {
    @RequestLine(value = "GET /{name}/{age}")
    Person getPersonByNameAndAgePathVariableExchange(@Param(value = "name") String name, @Param(value = "age") Integer age);
}
