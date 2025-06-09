package OnlineShopping;

import OnlineShopping.dto.SignUpRequestDTO;
import OnlineShopping.service.UserService;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GmailValidationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void testValidGmailAddress() {
        // Arrange
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .username("testuser")
                .email("testuser@gmail.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> userService.registerUser(dto));
    }

    @Test
    void testInvalidGmailAddress_NonGmailDomain() {
        // Arrange
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .username("testuser")
                .email("testuser@yahoo.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.registerUser(dto));
        assertTrue(exception.getMessage().contains("Email must be a valid Gmail address"));
    }

    @Test
    void testInvalidGmailAddress_InvalidFormat() {
        // Arrange
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .username("testuser")
                .email("invalid-email-format")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.registerUser(dto));
        assertTrue(exception.getMessage().contains("Email must be a valid Gmail address"));
    }

    @Test
    void testInvalidGmailAddress_NullEmail() {
        // Arrange
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .username("testuser")
                .email(null)
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.registerUser(dto));
        assertTrue(exception.getMessage().contains("Email must be a valid Gmail address"));
    }

    @Test
    void testValidGmailAddress_WithSpecialCharacters() {
        // Arrange
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .username("testuser")
                .email("test.user+tag@gmail.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> userService.registerUser(dto));
    }

    @Test
    void testValidGmailAddress_WithNumbers() {
        // Arrange
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .username("testuser")
                .email("testuser123@gmail.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> userService.registerUser(dto));
    }
} 