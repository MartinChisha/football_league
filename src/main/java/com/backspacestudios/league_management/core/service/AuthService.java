package com.backspacestudios.league_management.core.service;

import com.backspacestudios.league_management.core.dto.AuthResponse;
import com.backspacestudios.league_management.core.dto.SignupRequest;
import com.backspacestudios.league_management.core.entity.SuperAdmin;
import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.enums.UserStatus;
import com.backspacestudios.league_management.core.repository.SuperAdminRepository;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.core.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        return new AuthResponse(token, user.getUserId(), user.getEmail(), user.getRole().name());
    }

    @Transactional
    public AuthResponse register(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());  // UserRole
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setUserStatus(UserStatus.active);
        user.setFailedLoginAttempts(0);
        user.setMfaEnabled(false);

        user = userRepository.save(user);

        if (request.getRole() == UserRole.super_admin) {
            SuperAdmin superAdmin = new SuperAdmin();
            superAdmin.setUser(user);
           superAdmin.setSystemPermissions(new HashMap<>());
            superAdmin.setCanImpersonate(false);
            superAdmin.setSystemAccessLevel(com.backspacestudios.league_management.core.enums.SystemAccessLevel.full);
            superAdmin.setAssignedBy(null);
            superAdminRepository.save(superAdmin);
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .build();
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getUserId(), user.getEmail(), user.getRole().name());
    }
}