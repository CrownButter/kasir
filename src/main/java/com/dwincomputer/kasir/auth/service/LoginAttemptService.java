package com.dwincomputer.kasir.auth.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPTS = 5;
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> blockTimeCache = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            // Blokir selama 15 menit jika sudah 5 kali gagal
            blockTimeCache.put(username, LocalDateTime.now().plusMinutes(15));
        }
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockTimeCache.remove(username);
    }

    public boolean isBlocked(String username) {
        if (blockTimeCache.containsKey(username)) {
            if (LocalDateTime.now().isBefore(blockTimeCache.get(username))) {
                return true;
            }
            // Jika waktu blokir sudah lewat, hapus dari cache
            loginSucceeded(username);
        }
        return false;
    }
}