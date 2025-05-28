package com.vuelosapp.flightreservation.service;

import com.vuelosapp.flightreservation.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class LoginAttemptService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 5 * 60 * 1000; // 5 minutos en milisegundos

    @Autowired
    private UserService userService;

    public void loginSucceeded(String username) {
        userService.findByUsername(username).ifPresent(user -> {
            user.setFailedAttempts(0);
            user.setAccountLocked(false);
            user.setLockTime(null);
            userService.saveUser(user);
        });
    }

    public void loginFailed(String username) {
        userService.findByUsername(username).ifPresent(user -> {
            // No aplicar bloqueo a administradores
            if ("ADMIN".equals(user.getRole())) {
                return;
            }

            int failedAttempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(failedAttempts);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setLockTime(new Date());
            }
            userService.saveUser(user);
        });
    }


    public boolean isAccountLocked(String username) {
        return userService.findByUsername(username)
            .map(user -> {
                if (!user.isAccountLocked()) {
                    return false;
                }

                // Verificar si ha pasado el tiempo de bloqueo
                if (user.getLockTime() != null) {
                    long lockTimeInMillis = user.getLockTime().getTime();
                    long currentTimeInMillis = System.currentTimeMillis();

                    if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
                        // Desbloquear la cuenta después del tiempo de espera
                        user.setAccountLocked(false);
                        user.setFailedAttempts(0);
                        user.setLockTime(null);
                        userService.saveUser(user);
                        return false;
                    }
                }
                return true;
            })
            .orElse(false); // Si el usuario no existe, no está bloqueado
    }
}
