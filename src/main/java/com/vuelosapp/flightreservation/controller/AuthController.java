package com.vuelosapp.flightreservation.controller;

import com.vuelosapp.flightreservation.entity.User;
import com.vuelosapp.flightreservation.security.JwtUtil;
import com.vuelosapp.flightreservation.service.LoginAttemptService;
import com.vuelosapp.flightreservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vuelosapp.flightreservation.dto.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired 
    private UserService userService;
    @Autowired 
    private JwtUtil jwtUtil;
    @Autowired
    private LoginAttemptService loginAttemptService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Verificar si la cuenta está bloqueada
        if (loginAttemptService.isAccountLocked(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Cuenta bloqueada temporalmente. Por favor, intente más tarde.");
        }

        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // Reiniciar intentos fallidos después de un inicio de sesión exitoso
            loginAttemptService.loginSucceeded(request.getUsername());
            
            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            // Registrar intento fallido
            loginAttemptService.loginFailed(request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Usuario o contraseña incorrectos");
        }
    }
}
