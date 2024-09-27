package com.mylearning.journalapp.mymock;

import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.repository.UserRepository;
import com.mylearning.journalapp.service.UserDetailServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public class MockJunitRuleUserDetailServiceImplTests {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailServiceImpl userService;

    @Test
    public void testLoadUserByUsername(){
        // Arrange
        User user = User.builder().userName("raj").email("raj@gmail.com").password("rajencryptedpasswd").roles(List.of("USER")).build();

        // Mock the repository response
        Mockito.when(userRepository.findByUserName(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("raj");

        // Assert
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(user.getUserName(),userDetails.getUsername());  // assert the specific content

        // Verify findByUserName() was called exactly once
        Mockito.verify(userRepository,Mockito.times(1)).findByUserName(ArgumentMatchers.anyString());
    }
}
