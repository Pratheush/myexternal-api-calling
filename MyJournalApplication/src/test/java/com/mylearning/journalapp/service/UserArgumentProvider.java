package com.mylearning.journalapp.service;

import com.mylearning.journalapp.entity.User;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


import java.util.List;
import java.util.stream.Stream;

public class UserArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(User.builder().userName("XYZ").password("XYZ123").roles(List.of("USER")).email("XYZ@gmail.com").build()),
                Arguments.of(User.builder().userName("LMNO").password("LMNO123").roles(List.of("USER")).email("LMNO@gmail.com").build())

        );
    }
}
