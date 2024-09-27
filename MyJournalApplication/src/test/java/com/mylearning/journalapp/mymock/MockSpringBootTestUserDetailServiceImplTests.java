package com.mylearning.journalapp.mymock;

import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.repository.UserRepository;
import com.mylearning.journalapp.service.UserDetailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class MockSpringBootTestUserDetailServiceImplTests {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserDetailServiceImpl userService;

    @Test
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
