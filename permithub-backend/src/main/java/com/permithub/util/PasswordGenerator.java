package com.permithub.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;
    private static final int DEFAULT_LENGTH = 12;
    
    private final Random random = new SecureRandom();

    /**
     * Generate a random password with default length (12)
     */
    public String generateRandomPassword() {
        return generateRandomPassword(DEFAULT_LENGTH);
    }

    /**
     * Generate a random password with specified length
     */
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }
        
        StringBuilder password = new StringBuilder(length);
        
        // Ensure at least one character from each category
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL));
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(getRandomChar(ALL_CHARS));
        }
        
        // Shuffle the password
        return shuffleString(password.toString());
    }

    /**
     * Generate a password based on employee ID or name
     */
    public String generatePasswordFromId(String base) {
        String cleanBase = base.replaceAll("[^a-zA-Z0-9]", "");
        if (cleanBase.length() < 4) {
            return generateRandomPassword();
        }
        
        String basePart = cleanBase.substring(0, Math.min(4, cleanBase.length()));
        String randomPart = generateRandomPassword(8);
        
        return basePart + randomPart;
    }

    /**
     * Generate a memorable password (easier to remember)
     */
    public String generateMemorablePassword() {
        String[] words = {"Sun", "Moon", "Star", "Sky", "Cloud", "Rain", "Tree", "Book", 
                          "Door", "Wall", "Fish", "Bird", "Lion", "Bear", "Wolf", "Rose"};
        String[] numbers = {"123", "456", "789", "234", "567", "890"};
        String[] symbols = {"!", "@", "#", "$", "%", "&"};
        
        String word1 = words[random.nextInt(words.length)];
        String word2 = words[random.nextInt(words.length)];
        String number = numbers[random.nextInt(numbers.length)];
        String symbol = symbols[random.nextInt(symbols.length)];
        
        return word1 + word2 + number + symbol;
    }

    /**
     * Validate password strength
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*].*");
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    /**
     * Get password strength message
     */
    public String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        
        StringBuilder missing = new StringBuilder();
        if (!password.matches(".*[A-Z].*")) {
            missing.append("uppercase letter, ");
        }
        if (!password.matches(".*[a-z].*")) {
            missing.append("lowercase letter, ");
        }
        if (!password.matches(".*\\d.*")) {
            missing.append("digit, ");
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            missing.append("special character, ");
        }
        
        if (missing.length() > 0) {
            return "Missing: " + missing.substring(0, missing.length() - 2);
        }
        
        return "Strong password";
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private char getRandomChar(String characterSet) {
        int index = random.nextInt(characterSet.length());
        return characterSet.charAt(index);
    }

    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}