package com.airline.assistant.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Airline-specific tools for the AI assistant.
 * These tools simulate real-world airline operations for demonstration purposes.
 */
@Component
public class AirlineTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(AirlineTools.class);
    private final Random random = new Random();

    /**
     * Send SMS notification to user about flight updates or loyalty program information
     */
    @Tool(description = "Send SMS notification to user about flight updates, loyalty program information, or travel reminders")
    public String sendSmsNotification(String phoneNumber, String message) {
        LOGGER.info("Sending SMS to {}: {}", phoneNumber, message);
        
        // Simulate SMS sending with random delay
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate occasional failures for realism
        if (random.nextInt(10) == 0) {
            LOGGER.warn("SMS sending failed for {}", phoneNumber);
            return String.format("❌ Failed to send SMS to %s. Please try again later.", phoneNumber);
        }
        
        LOGGER.info("SMS sent successfully to {}", phoneNumber);
        return String.format("✅ SMS sent successfully to %s: \"%s\"", phoneNumber, message);
    }

    /**
     * Get flight information including status, gate, and timing details
     */
    @Tool(description = "Get comprehensive flight information including status, gate, departure/arrival times, and delays")
    public String getFlightInformation(String flightNumber, String date) {
        LOGGER.info("Looking up flight information for {} on {}", flightNumber, date);
        
        // Simulate API call delay
        try {
            Thread.sleep(800 + random.nextInt(1200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate flight data
        String[] statuses = {"On Time", "Delayed", "Boarding", "Departed", "Arrived", "Cancelled"};
        String[] gates = {"A12", "B7", "C15", "D3", "E9", "F21"};
        String[] aircraft = {"Boeing 737-800", "Airbus A320", "Boeing 777-300ER", "Airbus A350"};
        
        String status = statuses[random.nextInt(statuses.length)];
        String gate = gates[random.nextInt(gates.length)];
        String aircraftType = aircraft[random.nextInt(aircraft.length)];
        int delayMinutes = status.equals("Delayed") ? 15 + random.nextInt(120) : 0;
        
        return String.format("""
            ✈️ Flight Information for %s (%s)
            
            📊 Status: %s%s
            🚪 Gate: %s
            ✈️ Aircraft: %s
            🕐 Scheduled Departure: 14:30
            🕐 Scheduled Arrival: 17:45
            
            💺 Seat Map: Available online
            🧳 Baggage: Track via airline app
            """, 
            flightNumber, date, status, 
            delayMinutes > 0 ? String.format(" (%d min delay)", delayMinutes) : "",
            gate, aircraftType);
    }

    /**
     * Calculate loyalty points earned based on spending and member tier
     */
    @Tool(description = "Calculate loyalty points earned based on spending amount, member tier, and spending category")
    public String calculateLoyaltyPoints(String memberTier, double spendingAmount, String spendingCategory) {
        LOGGER.info("Calculating loyalty points for {} member spending ${} in {} category", 
                   memberTier, spendingAmount, spendingCategory);
        
        // Simulate calculation delay
        try {
            Thread.sleep(300 + random.nextInt(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Define earning rates by tier and category
        Map<String, Double> baseRates = Map.of(
            "Basic", 1.0,
            "Silver", 1.25,
            "Gold", 1.5,
            "Platinum", 2.0,
            "Diamond", 2.5
        );
        
        Map<String, Double> categoryMultipliers = Map.of(
            "flights", 2.0,
            "hotels", 1.5,
            "dining", 1.2,
            "shopping", 1.0,
            "gas", 1.1
        );
        
        double baseRate = baseRates.getOrDefault(memberTier, 1.0);
        double categoryMultiplier = categoryMultipliers.getOrDefault(spendingCategory.toLowerCase(), 1.0);
        double rate = baseRate * categoryMultiplier;
        int pointsEarned = (int) Math.round(spendingAmount * rate);
        
        return String.format("""
            💳 Loyalty Points Calculation
            
            👤 Member Tier: %s
            💰 Spending: $%.2f (%s)
            📈 Earning Rate: %.1fx
            🎯 Points Earned: %,d points
            
            💡 Tip: %s
            """, 
            memberTier, spendingAmount, spendingCategory, rate, pointsEarned,
            getEarningTip(spendingCategory));
    }

    /**
     * Add calendar event for flight or travel-related activities
     */
    @Tool(description = "Add travel-related event to user's calendar including flights, hotel check-ins, or travel reminders")
    public String addCalendarEvent(String eventTitle, String dateTime, String location) {
        LOGGER.info("Adding calendar event: {} at {} in {}", eventTitle, dateTime, location);
        
        // Simulate calendar API call
        try {
            Thread.sleep(400 + random.nextInt(600));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate occasional calendar conflicts
        if (random.nextInt(8) == 0) {
            return String.format("""
                ⚠️ Calendar Conflict Detected
                
                📅 Event: %s
                🕐 Time: %s
                📍 Location: %s
                
                ❓ You have another event at this time. Would you like to:
                • Reschedule this event
                • Override the conflict
                • Set a reminder instead
                """, eventTitle, dateTime, location);
        }
        
        return String.format("""
            ✅ Calendar Event Added Successfully
            
            📅 Event: %s
            🕐 Time: %s
            📍 Location: %s
            🔔 Reminder: Set for 1 hour before
            
            💡 Event details sent to your email
            """, eventTitle, dateTime, location);
    }

    /**
     * Check loyalty program status and benefits
     */
    @Tool(description = "Check current loyalty program status, tier benefits, points balance, and qualification progress")
    public String checkLoyaltyStatus(String memberNumber, String program) {
        LOGGER.info("Checking loyalty status for member {} in {} program", memberNumber, program);
        
        // Simulate loyalty system lookup
        try {
            Thread.sleep(600 + random.nextInt(800));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate realistic loyalty data
        String[] tiers = {"Basic", "Silver", "Gold", "Platinum", "Diamond"};
        String currentTier = tiers[random.nextInt(tiers.length)];
        int currentPoints = ThreadLocalRandom.current().nextInt(5000, 150000);
        int tierQualifyingMiles = ThreadLocalRandom.current().nextInt(10000, 75000);
        int nextTierThreshold = getTierThreshold(currentTier);
        int pointsToNext = Math.max(0, nextTierThreshold - tierQualifyingMiles);
        
        LocalDateTime expirationDate = LocalDateTime.now().plusMonths(12 + random.nextInt(12));
        String formattedExpiration = expirationDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        
        return String.format("""
            🏆 Loyalty Program Status - %s
            
            👤 Member: %s
            🎖️ Current Tier: %s
            💎 Points Balance: %,d points
            ✈️ Tier Qualifying Miles: %,d miles
            
            📈 Next Tier Progress:
            %s
            
            🎁 Current Benefits:
            %s
            
            📅 Points Expiration: %s
            
            💡 %s
            """, 
            program, memberNumber, currentTier, currentPoints, tierQualifyingMiles,
            pointsToNext > 0 ? String.format("🎯 %,d miles needed for next tier", pointsToNext) : "🌟 Congratulations! You've reached the highest tier!",
            getTierBenefits(currentTier),
            formattedExpiration,
            getLoyaltyTip(pointsToNext));
    }
    
    // Helper methods for realistic data generation
    
    private String getEarningTip(String category) {
        return switch (category.toLowerCase()) {
            case "flights" -> "Book directly with the airline for bonus points!";
            case "hotels" -> "Consider airline partner hotels for extra miles.";
            case "dining" -> "Use your airline credit card for additional rewards.";
            default -> "Check for seasonal bonus categories to maximize earnings.";
        };
    }
    
    private int getTierThreshold(String tier) {
        return switch (tier) {
            case "Basic" -> 25000;
            case "Silver" -> 50000;
            case "Gold" -> 75000;
            case "Platinum" -> 100000;
            default -> 0;
        };
    }
    
    private String getTierBenefits(String tier) {
        return switch (tier) {
            case "Basic" -> "• Earn miles on flights\n• Online account management";
            case "Silver" -> "• Priority check-in\n• Free checked bag\n• 25% bonus miles";
            case "Gold" -> "• Priority boarding\n• Lounge access\n• 50% bonus miles\n• Free upgrades";
            case "Platinum" -> "• Premium check-in\n• Unlimited lounge access\n• 75% bonus miles\n• Guaranteed upgrades";
            case "Diamond" -> "• Dedicated phone line\n• Global lounge access\n• 100% bonus miles\n• Complimentary upgrades";
            default -> "• Standard program benefits";
        };
    }
    
    private String getLoyaltyTip(int pointsToNext) {
        if (pointsToNext == 0) {
            return "Enjoy your elite benefits and consider the co-branded credit card for even more perks!";
        }
        return String.format("Tip: Book a round-trip flight to easily earn the remaining %,d miles for your next tier!", pointsToNext);
    }
}
