package OnlineShopping.entity.repository;


import OnlineShopping.dto.UserListResponseDTO;
import OnlineShopping.dto.UserRequestDTO;
import OnlineShopping.dto.UserResponseDTO;
import OnlineShopping.entity.User;

public interface UserService {
    UserListResponseDTO getAllUsers(int page, int size, String sortBy, String sortDir);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);
    UserResponseDTO deleteUser(Long id);
    UserListResponseDTO filterUsers(String role, String status, int page, int size, String sortBy, String sortDir);

} 