package me.dnvery.libruh.service;

import me.dnvery.libruh.config.JwtConfig;
import me.dnvery.libruh.dto.auth.AuthResponse;
import me.dnvery.libruh.dto.auth.LoginRequest;
import me.dnvery.libruh.dto.auth.RegisterRequest;
import me.dnvery.libruh.entity.RefreshToken;
import me.dnvery.libruh.entity.User;
import me.dnvery.libruh.enums.Role;
import me.dnvery.libruh.repository.RefreshTokenRepository;
import me.dnvery.libruh.repository.UserRepository;
import me.dnvery.libruh.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtConfig jwtConfig;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       JwtConfig jwtConfig) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtConfig = jwtConfig;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user = userRepository.save(user);

        return generateAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return generateAuthResponse(user);
    }

    public AuthResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        refreshTokenRepository.delete(refreshToken);

        return generateAuthResponse(user);
    }

    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(refreshTokenRepository::delete);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshTokenStr = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(jwtConfig.getRefreshExpiration());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenStr, user.getUsername(), user.getId());
    }
}