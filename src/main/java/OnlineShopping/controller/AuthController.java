package OnlineShopping.controller;

import OnlineShopping.dto.SignUpRequestDTO;
import OnlineShopping.entity.User;
import OnlineShopping.exception.UserAlreadyExistsException;
import OnlineShopping.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/")
    public String showLandingPage() {
        return "landing";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "registrationSuccess", required = false) String registrationSuccess,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }

        if (registrationSuccess != null) {
            model.addAttribute("registrationSuccess", "Registration successful! Please login.");
        }

        return "auth/login";
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new SignUpRequestDTO());
        return "auth/signup";
    }

    /**
     * Handles form-based registration (HTML form submission)
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") SignUpRequestDTO signUpRequestDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            // Use the UserService to register the user
            userService.registerUser(signUpRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/api/auth/login?registrationSuccess=true";
        } catch (UserAlreadyExistsException e) {
            // Handle specific user already exists exception
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/signup";
        } catch (Exception e) {
            // Handle other exceptions
            model.addAttribute("errorMessage", "An error occurred during registration: " + e.getMessage());
            return "auth/signup";
        }
    }

    /**
     * Handles REST API registration (JSON request)
     */
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            // Use the UserService to register the user
            User newUser = userService.registerUser(signUpRequestDTO);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", newUser.getId());
            response.put("username", newUser.getUsername());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred during registration: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/api/auth/login";
    }
}