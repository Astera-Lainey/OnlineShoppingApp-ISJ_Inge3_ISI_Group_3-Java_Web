package OnlineShopping.config.security;

import OnlineShopping.entity.User;
import OnlineShopping.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = Logger.getLogger(CustomAuthenticationSuccessHandler.class.getName());

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        try {
            String email = authentication.getName();
            logger.info("Authentication successful for user: " + email);
            
            // Get the actual User entity from the database using email
            User user = userDetailsService.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            
            String token = tokenProvider.generateToken(user);
            logger.info("Generated JWT token for user: " + email);

            // Create cookie with JWT token
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(request.isSecure()); // Set secure flag in production
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(jwtCookie);
            logger.info("JWT cookie set for user: " + email);

            // Add user info to response
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("role", user.getRole().name());

            // Store user info in session
            request.getSession().setAttribute("userInfo", userInfo);
            logger.info("User info stored in session for user: " + email);

            // Redirect based on role
            String redirectUrl = determineRedirectUrl(authentication.getAuthorities());
            logger.info("Redirecting user " + email + " to: " + redirectUrl);
            response.sendRedirect(request.getContextPath() + redirectUrl);
            
        } catch (Exception e) {
            logger.severe("Error during authentication success handling: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/api/auth/login?error=true");
        }
    }

    private String determineRedirectUrl(Collection<? extends GrantedAuthority> authorities) {
        logger.info("Determining redirect URL for authorities: " + authorities);
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            logger.info("Processing role: " + role);
            
            if (role.equals("ROLE_ADMIN")) {
                logger.info("Found ADMIN role, redirecting to admin dashboard");
                return "/admin/adminDashboard";
            } else if (role.equals("ROLE_USER")) {
                logger.info("Found USER role, redirecting to user dashboard");
                return "/user/main";
            }
        }
        
        logger.warning("No matching role found, defaulting to login page");
        return "/api/auth/login";
    }
} 