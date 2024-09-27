package com.mylearning.journalapp.mymock;


import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.repository.UserRepository;
import com.mylearning.journalapp.service.UserDetailServiceImpl;
import com.mylearning.journalapp.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

class MockInitUserDetailServiceImplTests {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailServiceImpl userService;

    @BeforeEach
    void initMockSetup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled
    void testLoadUserByUsername(){
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
