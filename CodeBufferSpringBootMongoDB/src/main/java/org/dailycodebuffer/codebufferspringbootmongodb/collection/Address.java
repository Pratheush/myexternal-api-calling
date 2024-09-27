package org.dailycodebuffer.codebufferspringbootmongodb.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@Profile(value = {"codebuffer","student"})
@AllArgsConstructor
public class Address {
    private String address1;
    private String address2;
    private String city;
}
