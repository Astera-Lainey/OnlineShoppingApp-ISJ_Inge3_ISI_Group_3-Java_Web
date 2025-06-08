package OnlineShopping.entity.repository;

import OnlineShopping.entity.ContactUs;
import OnlineShopping.entity.ContactUsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {

    List<ContactUs> findBySubject(String subject);

    List<ContactUs> findByEmailOrderByCreatedAtDesc(String email);

    long countByStatus(ContactUsStatus status);

}
