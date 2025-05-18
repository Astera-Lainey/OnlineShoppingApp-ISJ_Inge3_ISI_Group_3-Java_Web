package OnlineShopping.entity.repository;

import OnlineShopping.entity.Order;
import OnlineShopping.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrder(Order order);
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    List<Transaction> findByPaymentMethod(Transaction.PaymentMethod paymentMethod);
}