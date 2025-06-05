package com.airline.assistant.service;

import com.airline.assistant.model.User;
import com.airline.assistant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling user-related operations.
 */
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        LOGGER.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Check if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        LOGGER.debug("Checking if user exists by username: {}", username);
        return userRepository.existsByUsername(username);
    }

    /**
     * Save a user to the database.
     *
     * @param user the user to save
     * @return the saved user
     */
    public User saveUser(User user) {
        LOGGER.debug("Saving user: {}", user);
        return userRepository.save(user);
    }

    /**
     * Get user information as a formatted string for prompt augmentation.
     *
     * @param username the username to get information for
     * @return a formatted string with user information, or empty string if user not found
     */
    public String getUserInfoForPrompt(String username) {
        Optional<User> userOpt = findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            LOGGER.debug("Found user information for prompt augmentation: {}", user);

            StringBuilder userInfo = new StringBuilder();
            userInfo.append("User Information:\n");
            userInfo.append("- Username: ").append(user.getUsername()).append("\n");

            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                userInfo.append("- Last Name: ").append(user.getLastName()).append("\n");
            }

            if (user.getLoyaltyNumber() != null && !user.getLoyaltyNumber().isEmpty()) {
                userInfo.append("- Loyalty Number: ").append(user.getLoyaltyNumber()).append("\n");
            }

            if (user.getLoyaltyStatus() != null && !user.getLoyaltyStatus().isEmpty()) {
                userInfo.append("- Loyalty Status: ").append(user.getLoyaltyStatus()).append("\n");
            }

            if (user.getPreferredAirport() != null && !user.getPreferredAirport().isEmpty()) {
                userInfo.append("- Preferred Airport: ").append(user.getPreferredAirport()).append("\n");
            }

            if (user.getAirline() != null && !user.getAirline().isEmpty()) {
                userInfo.append("- Airline: ").append(user.getAirline()).append("\n");
            }

            return userInfo.toString();
        }

        LOGGER.debug("No user found with username: {}", username);
        return "";
    }
}
