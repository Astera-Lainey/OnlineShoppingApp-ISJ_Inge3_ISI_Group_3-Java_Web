package OnlineShopping.dto;

import OnlineShopping.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String email;
    private String role;
    private String redirectUrl;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.redirectUrl = user.getRole() == User.Role.ADMIN ? "/admin/dashboard" : "/user/dashboard";
    }
} 