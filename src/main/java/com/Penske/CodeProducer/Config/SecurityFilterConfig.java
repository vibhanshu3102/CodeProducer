package com.Penske.CodeProducer.Config;

import com.Penske.CodeProducer.JWT.JWTAuthenticationFilter;
import com.Penske.CodeProducer.JWT.JwtAuthenticationEntryPoint;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityFilterConfig {
    private final JwtAuthenticationEntryPoint point;
    private final JWTAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        return security
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .cors(cors -> cors.disable()) // Disable CORS (configure as needed)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authenticate", "/api-docs" , "/swagger-ui-custom.html").permitAll() // Allow unauthenticated access to /authenticate and /api-docs
                        .requestMatchers("/sendCode").hasRole("ADMIN") // Require ADMIN role for /sendCode
                        .requestMatchers("/filterData", "/getcodeByLatestVersion").hasRole("USER") // Require USER role for /filterData and /getcodeByLatestVersion
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point)) // Handle authentication exceptions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class) // Add custom JWT filter before UsernamePasswordAuthenticationFilter
                .build();
    }
}
