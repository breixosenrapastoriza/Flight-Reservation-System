package com.vuelosapp.flightreservation.security;

import com.vuelosapp.flightreservation.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver resolver;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, 
                           UserDetailsService userDetailsService,
                           @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(AUTH_HEADER);
            
            if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }
            
            String jwt = authHeader.substring(TOKEN_PREFIX.length());
            
            if (jwt.isEmpty()) {
                throw new JwtAuthenticationException("Token JWT vacío");
            }
            
            String username = jwtUtil.extractUsername(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (UsernameNotFoundException e) {
                    throw new JwtAuthenticationException("Usuario no encontrado: " + username, e);
                }
            }
            
            chain.doFilter(request, response);
            
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
            resolver.resolveException(request, response, null, e);
        } catch (SignatureException | MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
            resolver.resolveException(request, response, null, 
                new JwtAuthenticationException("Token JWT inválido o mal formado", e));
        } catch (JwtAuthenticationException e) {
            logger.error("Error de autenticación: {}", e.getMessage());
            resolver.resolveException(request, response, null, e);
        } catch (Exception e) {
            logger.error("Error en el filtro JWT: {}", e.getMessage(), e);
            resolver.resolveException(request, response, null, 
                new JwtAuthenticationException("Error en la autenticación", e));
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Añade aquí los paths que no requieran autenticación
        return path.startsWith("/auth/");
    }
}

