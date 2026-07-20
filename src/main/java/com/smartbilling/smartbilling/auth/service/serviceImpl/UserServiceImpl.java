package com.smartbilling.smartbilling.auth.service.serviceImpl;

import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.repository.RefreshTokenRepository;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import com.smartbilling.smartbilling.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        User existing = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        refreshTokenRepository.deleteByUserId(existing.getId());
        userRepository.delete(existing);
        log.info("Utilisateur supprimé : {}", user.getEmail());
    }

    @Override
    public User updateUser(User user) {
        User existing = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existing.setEmail(user.getEmail());
        existing.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(existing);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}