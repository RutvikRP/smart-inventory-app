package com.smartinventory.inventory.controller;

import com.smartinventory.inventory.dto.AuthRequestDTO;
import com.smartinventory.inventory.dto.AuthResponseDTO;
import com.smartinventory.inventory.dto.RegisterRequestDTO;
import com.smartinventory.inventory.entity.*;
import com.smartinventory.inventory.repository.UserRepository;
import com.smartinventory.inventory.util.JwtUtils;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }
        if (userRepository.existsByEmail(dto.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already used");
        }
        User u = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .roles(Set.of(Role.valueOf(dto.role()))) // ensure client uses ROLE_ADMIN etc.
                .build();
        userRepository.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
        var user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream().map(a -> a.getAuthority()).toList());
        String access = jwtUtil.generateAccessToken(user.getUsername(), claims);
        String refresh = jwtUtil.generateRefreshToken(user.getUsername());
        long expiresIn = jwtUtil.extractExpiryDate(access).getTime() - System.currentTimeMillis();
        return ResponseEntity.ok(new AuthResponseDTO(access, refresh, "Bearer", expiresIn));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody Map<String,String> body) {
        String refresh = body.get("refreshToken");
        try {
            String username = jwtUtil.extractUsername(refresh);
            // Optionally verify refresh token revoked
            Map<String,Object> claims = new HashMap<>();
            // load roles if needed from DB
            String access = jwtUtil.generateAccessToken(username, claims);
            String newRefresh = jwtUtil.generateRefreshToken(username);
            long expiresIn = jwtUtil.extractExpiryDate(access).getTime() - System.currentTimeMillis();
            return ResponseEntity.ok(new AuthResponseDTO(access, newRefresh, "Bearer", expiresIn));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Logout: if you want true invalidation, persist refresh token and mark revoked (or put token in blacklist)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String,String> body) {
        // Example: accept refresh token and blacklist it (requires DB/Redis)
        return ResponseEntity.noContent().build();
    }
}
