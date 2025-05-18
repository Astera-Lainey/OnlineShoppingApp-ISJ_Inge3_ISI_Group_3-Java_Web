package OnlineShopping.entity.services;

import OnlineShopping.entity.User;
import java.util.List;

public interface AdminUserService {
    List<User> getAllUsers();
    User getUserById(Long userId);
    User updateUser(Long userId, User updatedUser);
    void deleteUser(Long userId);
    void blockUser(Long userId);
    void unblockUser(Long userId);
}