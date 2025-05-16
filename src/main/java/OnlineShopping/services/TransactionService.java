
package OnlineShopping.services;

import OnlineShopping.entity.Order;
import OnlineShopping.entity.Transaction;

public interface TransactionService {
    Transaction processPayment(Long orderId, Transaction.PaymentMethod paymentMethod, Double amount);
    Transaction getTransactionById(Long transactionId);
    Transaction getTransactionByOrderId(Long orderId);
    void updateTransactionStatus(Long transactionId, Transaction.TransactionStatus status);
}