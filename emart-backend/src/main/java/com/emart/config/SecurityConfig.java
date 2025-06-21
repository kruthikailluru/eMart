package com.emart.config;

import com.emart.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtService jwtService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/validate-token").permitAll()
                .requestMatchers("/api/barcodes/info").permitAll()
                .requestMatchers("/api/products/available", "/api/products/approved").permitAll()
                .requestMatchers("/api/products/barcode/**").permitAll()
                .requestMatchers("/api/products/search").permitAll()
                .requestMatchers("/api/products/price-range").permitAll()
                .requestMatchers("/api/products/{productId}").permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/products/pending").hasRole("ADMIN")
                .requestMatchers("/api/products/expired").hasRole("ADMIN")
                .requestMatchers("/api/products/low-stock").hasRole("ADMIN")
                .requestMatchers("/api/products/{productId}/approve").hasRole("ADMIN")
                .requestMatchers("/api/products/{productId}/reject").hasRole("ADMIN")
                .requestMatchers("/api/products/{productId}/stock").hasRole("ADMIN")
                .requestMatchers("/api/orders/pending").hasRole("ADMIN")
                .requestMatchers("/api/orders/pending-payments").hasRole("ADMIN")
                .requestMatchers("/api/orders/{orderId}/status").hasRole("ADMIN")
                .requestMatchers("/api/orders/{orderId}/payment-status").hasRole("ADMIN")
                .requestMatchers("/api/orders/revenue/**").hasRole("ADMIN")
                .requestMatchers("/api/payments/{paymentId}/refund").hasRole("ADMIN")
                .requestMatchers("/api/payments/{paymentId}/status").hasRole("ADMIN")
                .requestMatchers("/api/payments/revenue/**").hasRole("ADMIN")
                .requestMatchers("/api/payments/methods/summary").hasRole("ADMIN")
                .requestMatchers("/api/invoices/{invoiceId}/sign").hasRole("ADMIN")
                .requestMatchers("/api/invoices/{invoiceId}/status").hasRole("ADMIN")
                .requestMatchers("/api/invoices/{invoiceId}/send").hasRole("ADMIN")
                .requestMatchers("/api/invoices/overdue").hasRole("ADMIN")
                .requestMatchers("/api/invoices/revenue/**").hasRole("ADMIN")
                .requestMatchers("/api/invoices/summary").hasRole("ADMIN")
                
                // Supplier endpoints
                .requestMatchers("/api/products").hasRole("SUPPLIER")
                .requestMatchers("/api/products/{productId}").hasAnyRole("SUPPLIER", "ADMIN")
                .requestMatchers("/api/products/supplier").hasRole("SUPPLIER")
                
                // Customer endpoints
                .requestMatchers("/api/orders").hasRole("CUSTOMER")
                .requestMatchers("/api/orders/customer").hasRole("CUSTOMER")
                .requestMatchers("/api/orders/{orderId}/cancel").hasRole("CUSTOMER")
                .requestMatchers("/api/payments/process").hasRole("CUSTOMER")
                .requestMatchers("/api/payments/customer").hasRole("CUSTOMER")
                .requestMatchers("/api/invoices/customer").hasRole("CUSTOMER")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
} 