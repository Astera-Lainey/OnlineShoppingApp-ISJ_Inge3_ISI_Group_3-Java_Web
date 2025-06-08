package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String Name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String Phone;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String Message;

    @Enumerated(EnumType.STRING)
    private ContactUsStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
