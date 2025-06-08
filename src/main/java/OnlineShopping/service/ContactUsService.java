package OnlineShopping.service;

import OnlineShopping.entity.Admin;
import OnlineShopping.entity.ContactUs;
import OnlineShopping.entity.ContactUsStatus;
import OnlineShopping.entity.repository.AdminDashboardRepository;
import OnlineShopping.entity.repository.ContactUsRepository;
import OnlineShopping.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactUsService {

    @Autowired
    private ContactUsRepository contactUsRepository;

//    @Autowired
//    private AdminDashboardRepository adminDashboardRepository;

    public ContactUs saveContactUs(ContactUs contactUs) {
        contactUs.setStatus(ContactUsStatus.PENDING);
        ContactUs savedContactUs = contactUsRepository.save(contactUs);
        sendAdminNotification(savedContactUs);

        return savedContactUs;
    }

    private void sendAdminNotifications(ContactUs savedContactUs) {
    }

    public List<ContactUs> getAllContactUs(String email) {
        return contactUsRepository.findAll();
    }

    public long countAllContactUs(String email) {
        return contactUsRepository.countByStatus(ContactUsStatus.PENDING);
    }

    public List<ContactUs> getRecentContactUs(String email) {
        return contactUsRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    public List<ContactUs> getContactUsByEmail(String email) {
        return contactUsRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    public ContactUs getFeedbackById(Integer id) {
        return contactUsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }

    public ContactUs updateContactUsStatus(Integer id, ContactUsStatus status) {
        ContactUs contactUs = getFeedbackById(id);
        contactUs.setStatus(status);
        return contactUsRepository.save(contactUs);
    }

    public void deleteContactUs(Integer id) {
        contactUsRepository.deleteById(id);
    }

    private void sendAdminNotification(ContactUs contactUs) {
        try {
            String subject = "New Feedback Received: " + contactUs.getSubject();
            String body = String.format(
                    "New feedback received from %s (%s)\n\n" +
                            "Type: %s\n" +
                            "Subject: %s\n" +
                            "Message: %s\n\n" +
                            "Submitted at: %s",
                    contactUs.getName(),
                    contactUs.getEmail(),
                    contactUs.getSubject(),
                    contactUs.getMessage()
            );

//            // Send email to admin
//            Object emailService = new Object();
//            emailService.sendEmail("admin@yourstore.com", subject, body);
//        } catch (Exception e) {
//            // Log error but don't fail the feedback submission
//            System.err.println("Failed to send admin notification: " + e.getMessage());
//        }
//}
        } finally {

        }
    }

//    public List<ContactUs> getFeedbacksByEmail(String userEmail) {
//        return contactUsRepository.findByEmailOrderByCreatedAtDesc(userEmail);
//    }
}