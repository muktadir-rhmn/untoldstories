package me.untoldstories.be.user;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
class PasswordHasher {
    private MessageDigest md;

    public PasswordHasher() {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String hash(String password) {
        byte[] bytes = md.digest(password.getBytes());
        StringBuilder generatedPassword = new StringBuilder();

        for (byte aByte : bytes) {
            generatedPassword.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return generatedPassword.toString();
    }

}
