package com.airline.assistant.config;

import com.airline.assistant.model.User;
import com.airline.assistant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for initializing sample data in the database.
 */
@Configuration
public class DataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    /**
     * Creates a CommandLineRunner bean that initializes sample user data.
     *
     * @param userRepository the repository for User entities
     * @return a CommandLineRunner that initializes sample data
     */
    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            LOGGER.info("Initializing sample user data...");

            // Check if users already exist to avoid duplicates
            if (userRepository.count() == 0) {
                // Create sample users
                User john = new User("John", "Smith", "DL123456", "Gold", "ATL", "Delta");
                User sarah = new User("Sarah", "Johnson", "UA789012", "Premier 1K", "SFO", "United");
                User michael = new User("Michael", "Brown", "DL456789", "Silver", "JFK", "Delta");
                User emma = new User("Emma", "Davis", "UA234567", "Premier Gold", "ORD", "United");
                User david = new User("David", "Wilson", "DL890123", "Platinum", "LAX", "Delta");

                // Save users to the database
                userRepository.save(john);
                userRepository.save(sarah);
                userRepository.save(michael);
                userRepository.save(emma);
                userRepository.save(david);

                LOGGER.info("Sample user data initialized with {} users", userRepository.count());
            } else {
                LOGGER.info("User data already exists, skipping initialization");
            }
        };
    }
}
