package com.Penske.CodeProducer.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/api/sendCode").hasRole("USER")
                .requestMatchers("/api/filterData").permitAll()
                .requestMatchers("/api/getcodeByLatestVersion").permitAll()
                .requestMatchers("/api/getLatestVersionOfCode").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic(Customizer.withDefaults()) // Enable basic authentication
                .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity in testing with Postman

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}password") // {noop} means no password encoding
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
