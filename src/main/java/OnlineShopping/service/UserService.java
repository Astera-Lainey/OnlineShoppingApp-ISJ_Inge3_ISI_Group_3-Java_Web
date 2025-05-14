package OnlineShopping.service;

import OnlineShopping.dto.SignUpRequestDTO;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user from SignUpRequestDTO
     * @param registrationDto User registration information
     * @return The created user
     * @throws UserAlreadyExistsException If username or email already exists
     */
    public User registerUser(SignUpRequestDTO registrationDto) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .role(User.Role.USER) // Default role for new users
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * Find user by email
     * @param email User email
     * @return User if found
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    /**
     * Find user by username
     * @param username Username
     * @return User if found
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }
}