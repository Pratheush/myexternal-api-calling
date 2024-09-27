package org.dailycodebuffer.codebufferspringbootmongodb.collection;

import lombok.Getter;
import org.springframework.context.annotation.Profile;

@Getter
@Profile({"student"})
public enum Gender {
    MALE("male"),FEMALE("female");

    private final String gndr;

    private Gender(String gndr) {
        this.gndr = gndr;
    }

}
