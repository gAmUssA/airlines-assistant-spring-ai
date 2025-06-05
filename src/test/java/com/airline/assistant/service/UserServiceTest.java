package com.airline.assistant.service;

import com.airline.assistant.model.User;
import com.airline.assistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    public void testFindByUsername_WhenUserExists() {
        // Arrange
        User mockUser = new User("John", "Smith", "DL123456", "Gold", "ATL", "Delta");
        when(userRepository.findByUsername("John")).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = userService.findByUsername("John");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getUsername());
        assertEquals("Smith", result.get().getLastName());
        assertEquals("DL123456", result.get().getLoyaltyNumber());
        assertEquals("Gold", result.get().getLoyaltyStatus());
        assertEquals("ATL", result.get().getPreferredAirport());
        assertEquals("Delta", result.get().getAirline());
        verify(userRepository, times(1)).findByUsername("John");
    }

    @Test
    public void testFindByUsername_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUsername("Unknown");

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("Unknown");
    }

    @Test
    public void testExistsByUsername_WhenUserExists() {
        // Arrange
        when(userRepository.existsByUsername("John")).thenReturn(true);

        // Act
        boolean result = userService.existsByUsername("John");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("John");
    }

    @Test
    public void testExistsByUsername_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.existsByUsername("Unknown")).thenReturn(false);

        // Act
        boolean result = userService.existsByUsername("Unknown");

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername("Unknown");
    }

    @Test
    public void testSaveUser() {
        // Arrange
        User user = new User("John", "Smith", "DL123456", "Gold", "ATL", "Delta");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.saveUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getUsername());
        assertEquals("Smith", result.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetUserInfoForPrompt_WhenUserExists() {
        // Arrange
        User mockUser = new User("John", "Smith", "DL123456", "Gold", "ATL", "Delta");
        when(userRepository.findByUsername("John")).thenReturn(Optional.of(mockUser));

        // Act
        String result = userService.getUserInfoForPrompt("John");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("User Information"));
        assertTrue(result.contains("Username: John"));
        assertTrue(result.contains("Last Name: Smith"));
        assertTrue(result.contains("Loyalty Number: DL123456"));
        assertTrue(result.contains("Loyalty Status: Gold"));
        assertTrue(result.contains("Preferred Airport: ATL"));
        assertTrue(result.contains("Airline: Delta"));
        verify(userRepository, times(1)).findByUsername("John");
    }

    @Test
    public void testGetUserInfoForPrompt_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        // Act
        String result = userService.getUserInfoForPrompt("Unknown");

        // Assert
        assertEquals("", result);
        verify(userRepository, times(1)).findByUsername("Unknown");
    }

    @Test
    public void testGetUserInfoForPrompt_WithPartialUserInfo() {
        // Arrange - user with only some fields populated
        User partialUser = new User("Sarah", null, null, "Silver", null, null);
        when(userRepository.findByUsername("Sarah")).thenReturn(Optional.of(partialUser));

        // Act
        String result = userService.getUserInfoForPrompt("Sarah");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("User Information"));
        assertTrue(result.contains("Username: Sarah"));
        assertTrue(result.contains("Loyalty Status: Silver"));
        assertFalse(result.contains("Last Name"));
        assertFalse(result.contains("Loyalty Number"));
        assertFalse(result.contains("Preferred Airport"));
        assertFalse(result.contains("Airline"));
        verify(userRepository, times(1)).findByUsername("Sarah");
    }
}
