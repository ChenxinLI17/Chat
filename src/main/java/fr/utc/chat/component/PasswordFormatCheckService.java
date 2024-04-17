package fr.utc.chat.component;

import org.springframework.stereotype.Service;

@Service
public class PasswordFormatCheckService {
    public boolean isPasswordValid(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUppercase && hasLowercase && hasDigit;
    }
}
