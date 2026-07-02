package com.backspacestudios.league_management.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.backspacestudios.league_management.core.service.CustomUserDetailsService;
import com.backspacestudios.league_management.core.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  
    private final CustomUserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // Allow preflight
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/uploads/**").permitAll()
            .requestMatchers("/api/marketplace/products/available").permitAll()
            .requestMatchers("/api/marketplace/products/category/**").permitAll()
            .requestMatchers("/api/marketplace/products/{productId}").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/marketplace/stores").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/marketplace/stores/all").hasRole("super_admin")
            .requestMatchers(HttpMethod.GET, "/api/marketplace/stores/my-store").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/marketplace/stores/*").permitAll()
            .requestMatchers("/api/marketplace/store-applications/apply").authenticated()
            .requestMatchers("/api/marketplace/store-applications/pending").hasRole("super_admin")
            .requestMatchers("/api/marketplace/store-applications/*/approve").hasRole("super_admin")
            .requestMatchers("/", "/survey.html", "/report.html", "/api/survey/**").permitAll()
            .requestMatchers("/favicon.ico", "/error").permitAll()
            .anyRequest().authenticated()
        )
        .userDetailsService(userDetailsService)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
    // CORS configuration bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(
        "http://localhost:3000",
        "http://0.0.0.0:8080",
         "http://localhost:8080",
         "http://localhost:5173", 
         "http://localhost:5174",             // 👈 Vite dev server
        "http://127.0.0.1:5173",          // 👈 Vite dev server
          "http://127.0.0.1:8080",
          "http://10.205.141.225:8080",
          "exp://10.205.141.225:8081",
          "exp://10.205.141.225:8082",
          "http://localhost:53052",        // 👈 add this
        "http://127.0.0.1:53052"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}