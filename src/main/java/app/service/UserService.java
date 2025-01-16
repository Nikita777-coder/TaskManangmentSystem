package app.service;

import app.entity.UserEntity;
import app.entity.userattributes.Permission;
import app.entity.userattributes.Role;
import app.mapper.UserMapper;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserService {
    private final UserRepository userRepository;
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDetails createUser(UserEntity entity) {
        if (userRepository.findByEmail(entity.getEmail()).isPresent()) {
            throw new IllegalArgumentException("user with this email exists");
        }

        return userRepository.save(entity);
    }
    public UserDetails getUser(String email) {
        return getUserByEmail(email);
    }

    public UserEntity getUser(UserDetails userDetails) {
        return getUserByEmail(userDetails.getUsername());
    }
    public boolean hasUserRequiredPermissions(UserEntity user, List<Permission> permissions) {
        for (Permission permission : permissions) {
            if (!user.getRole().getPermissions().contains(permission)) {
                return false;
            }
        }

        return true;
    }

    private UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
