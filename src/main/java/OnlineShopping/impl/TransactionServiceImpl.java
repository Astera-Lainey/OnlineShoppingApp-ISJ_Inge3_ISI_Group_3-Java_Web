package OnlineShopping.impl;

import OnlineShopping.entity.Order;
import OnlineShopping.entity.Transaction;
import OnlineShopping.repository.OrderRepository;
import OnlineShopping.repository.TransactionRepository;
import OnlineShopping.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Transaction processPayment(Long orderId, Transaction.PaymentMethod paymentMethod, Double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Check if transaction already exists for this order
        Optional<Transaction> existingTransactionOpt = transactionRepository.findByOrder(order);
        if (existingTransactionOpt.isPresent()) {
            throw new IllegalStateException("Transaction already exists for order with ID: " + orderId);
        }

        // Validate order amount
        if (!amount.equals(order.getOrderTotal())) {
            throw new IllegalArgumentException("Payment amount must match order total");
        }

        // Create new transaction
        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setAmount(amount);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Process payment logic would go here
        // This would typically involve calling a payment gateway API

        // For demo purposes, let's simulate a successful payment
        savedTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);

        // Update order status
        order.setOrderStatus(Order.OrderStatus.PROCESSING);
        orderRepository.save(order);

        return transactionRepository.save(savedTransaction);
    }

    @Override
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
    }

    @Override
    public Transaction getTransactionByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        return transactionRepository.findByOrder(order)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for order with ID: " + orderId));
    }

    @Override
    @Transactional
    public void updateTransactionStatus(Long transactionId, Transaction.TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));

        transaction.setStatus(status);

        // If transaction is marked as refunded
        if (status == Transaction.TransactionStatus.REFUNDED) {
            // Update corresponding order status
            Order order = transaction.getOrder();
            order.setOrderStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        transactionRepository.save(transaction);
    }
}
