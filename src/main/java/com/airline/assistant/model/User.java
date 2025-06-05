package com.airline.assistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Entity representing a user in the system.
 * Contains information about the user's loyalty program details.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "loyalty_number")
    private String loyaltyNumber;

    @Column(name = "loyalty_status")
    private String loyaltyStatus;

    @Column(name = "preferred_airport")
    private String preferredAirport;

    @Column(name = "airline")
    private String airline;

    @Column(name = "last_name")
    private String lastName;

    // Default constructor required by JPA
    public User() {
    }

    // Constructor with fields
    public User(String username, String lastName, String loyaltyNumber, String loyaltyStatus, String preferredAirport, String airline) {
        this.username = username;
        this.lastName = lastName;
        this.loyaltyNumber = loyaltyNumber;
        this.loyaltyStatus = loyaltyStatus;
        this.preferredAirport = preferredAirport;
        this.airline = airline;
    }

    // Constructor without lastName and airline for backward compatibility
    public User(String username, String loyaltyNumber, String loyaltyStatus, String preferredAirport, String airline) {
        this(username, null, loyaltyNumber, loyaltyStatus, preferredAirport, airline);
    }

    // Constructor without airline for backward compatibility
    public User(String username, String loyaltyNumber, String loyaltyStatus, String preferredAirport) {
        this(username, null, loyaltyNumber, loyaltyStatus, preferredAirport, null);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoyaltyNumber() {
        return loyaltyNumber;
    }

    public void setLoyaltyNumber(String loyaltyNumber) {
        this.loyaltyNumber = loyaltyNumber;
    }

    public String getLoyaltyStatus() {
        return loyaltyStatus;
    }

    public void setLoyaltyStatus(String loyaltyStatus) {
        this.loyaltyStatus = loyaltyStatus;
    }

    public String getPreferredAirport() {
        return preferredAirport;
    }

    public void setPreferredAirport(String preferredAirport) {
        this.preferredAirport = preferredAirport;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", lastName='" + lastName + '\'' +
                ", loyaltyNumber='" + loyaltyNumber + '\'' +
                ", loyaltyStatus='" + loyaltyStatus + '\'' +
                ", preferredAirport='" + preferredAirport + '\'' +
                ", airline='" + airline + '\'' +
                '}';
    }
}
