package OnlineShopping.service.impl;

import OnlineShopping.dto.UserDTO;
import OnlineShopping.dto.UserListResponseDTO;
import OnlineShopping.dto.UserRequestDTO;
import OnlineShopping.dto.UserResponseDTO;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.entity.repository.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserListResponseDTO getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageable);
        
        List<UserDTO> userDTOs = usersPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return new UserListResponseDTO(
            true,
            "Users retrieved successfully",
            userDTOs,
            usersPage.getNumber(),
            usersPage.getSize(),
            usersPage.getTotalElements(),
            usersPage.getTotalPages(),
            usersPage.isFirst(),
            usersPage.isLast()
        );
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            UserDTO userDTO = convertToDTO(userOptional.get());
            return new UserResponseDTO(true, "User retrieved successfully", userDTO);
        }
        return new UserResponseDTO(false, "User not found", null);
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        System.out.println("Creating user with data: " + userRequestDTO);
        
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            System.out.println("Username already exists: " + userRequestDTO.getUsername());
            return new UserResponseDTO(false, "Username already exists", null);
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            System.out.println("Email already exists: " + userRequestDTO.getEmail());
            return new UserResponseDTO(false, "Email already exists", null);
        }

        try {
            User user = new User();
            updateUserFromDTO(user, userRequestDTO);
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            System.out.println("Saving user: " + user);
            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());
            UserDTO userDTO = convertToDTO(savedUser);
            return new UserResponseDTO(true, "User created successfully", userDTO);
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return new UserResponseDTO(false, "Error creating user: " + e.getMessage(), null);
        }
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        System.out.println("Starting update for user ID: " + id);
        System.out.println("Update data received: " + userRequestDTO);
        
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            System.out.println("User not found with ID: " + id);
            return new UserResponseDTO(false, "User not found", null);
        }

        User existingUser = userOptional.get();
        System.out.println("Found existing user: " + existingUser);

        // Check for duplicate username only if username is being changed
        if (!existingUser.getUsername().equals(userRequestDTO.getUsername()) 
            && userRepository.existsByUsername(userRequestDTO.getUsername())) {
            System.out.println("Username already exists: " + userRequestDTO.getUsername());
            return new UserResponseDTO(false, "Username already exists", null);
        }

        // Check for duplicate email only if email is being changed
        if (!existingUser.getEmail().equals(userRequestDTO.getEmail()) 
            && userRepository.existsByEmail(userRequestDTO.getEmail())) {
            System.out.println("Email already exists: " + userRequestDTO.getEmail());
            return new UserResponseDTO(false, "Email already exists", null);
        }

        try {
            // Update user fields
            existingUser.setUsername(userRequestDTO.getUsername());
            existingUser.setEmail(userRequestDTO.getEmail());
            existingUser.setFirstName(userRequestDTO.getFirstName());
            existingUser.setLastName(userRequestDTO.getLastName());
            
            // Handle role and status enums
            if (userRequestDTO.getRole() != null) {
                existingUser.setRole(User.Role.valueOf(userRequestDTO.getRole().toString()));
            }
            
            // Only update password if a new one is provided
            if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
                System.out.println("Updating password for user ID: " + id);
                existingUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            }

            System.out.println("Saving updated user: " + existingUser);
            User updatedUser = userRepository.save(existingUser);
            System.out.println("User saved successfully: " + updatedUser);

            UserDTO userDTO = convertToDTO(updatedUser);
            return new UserResponseDTO(true, "User updated successfully", userDTO);
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return new UserResponseDTO(false, "Error updating user: " + e.getMessage(), null);
        }
    }

    @Override
    public UserResponseDTO deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return new UserResponseDTO(false, "User not found", null);
        }
        userRepository.deleteById(id);
        return new UserResponseDTO(true, "User deleted successfully", null);
    }

    @Override
    public UserListResponseDTO filterUsers(String role, String status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage;
        
        if (role != null) {
            usersPage = userRepository.findByRole(
                User.Role.valueOf(role.toUpperCase()),
                pageable
            );
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        
        List<UserDTO> userDTOs = usersPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return new UserListResponseDTO(
            true,
            "Users filtered successfully",
            userDTOs,
            usersPage.getNumber(),
            usersPage.getSize(),
            usersPage.getTotalElements(),
            usersPage.getTotalPages(),
            usersPage.isFirst(),
            usersPage.isLast()
        );
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
        userDTO.setFullName(user.getFirstName() + " " + user.getLastName());
        return userDTO;
    }

    private void updateUserFromDTO(User user, UserRequestDTO userRequestDTO) {
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setRole(User.Role.valueOf(userRequestDTO.getRole().toString()));
    }
} 