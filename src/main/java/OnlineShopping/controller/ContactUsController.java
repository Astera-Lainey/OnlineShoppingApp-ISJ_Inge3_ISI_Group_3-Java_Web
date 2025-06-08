package OnlineShopping.controller;

import OnlineShopping.entity.ContactUs;
import OnlineShopping.service.ContactUsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/contactUs")
public class ContactUsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContactUsService contactUsService;

    //Show contactus
    @GetMapping("/contactUs")
    public String showContactUsForm(Model model) {
        model.addAttribute("contactUs", new ContactUs());
        return "contact";
    }

    //Submit feedback
    @PostMapping("/submit")
    public String submitContactUs(@Valid @ModelAttribute ContactUs contactUs, RedirectAttributes redirectAttributes, Model model) {
        try {
            contactUsService.saveContactUs(contactUs);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thank you for your feedback! We'll get back to you soon.");
            return "redirect:/feedback/success";
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                    "Sorry, there was an error submitting your feedback. Please try again.");
            return "contact";
        }
    }

    // User's feedback history (if user is logged in)
    @GetMapping("/my-feedbacks")

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        Map<String, Object> response = new HashMap<>();
        logger.info("🛒 addToCart() was called");

        // Implement based on your authentication system
        // This could be from session, JWT token, or Spring Security

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authenticated user {}", auth);
        String userEmail = auth.getName();
        logger.info("DEBUG: Authenticated user email = {}", userEmail);
        return ResponseEntity.ok(response);
    }
}





