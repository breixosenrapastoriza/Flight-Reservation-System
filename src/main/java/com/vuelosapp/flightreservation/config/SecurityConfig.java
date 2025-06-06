package com.vuelosapp.flightreservation.config;

import com.vuelosapp.flightreservation.security.JwtRequestFilter;
import com.vuelosapp.flightreservation.security.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración CSRF
        http.csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // Deshabilitar CSRF para endpoints públicos y de autenticación
            .ignoringRequestMatchers(
                "/auth/**",
                "/h2-console/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            )
        );

        // Configuración de autorización
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/**",
                "/h2-console/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .requestMatchers("/api/flights/search").permitAll()
            .requestMatchers("/api/flights/{id}").permitAll()
            .requestMatchers("/api/flights/*/status").hasRole("ADMIN")
            .anyRequest().authenticated()
        );

        // Configuración de sesión
        http.sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Añadir filtros
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Configuración de cabeceras
        http.headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        // Configuración CORS
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList("*"));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-xsrf-token", "x-csrf-token"));
            return config;
        }));

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration() {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitFilter);
        registration.addUrlPatterns("/auth/login");
        registration.setOrder(1); // Alta prioridad
        return registration;
    }
}