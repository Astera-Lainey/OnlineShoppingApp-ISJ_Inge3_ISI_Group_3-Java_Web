-- Create reviews table
CREATE TABLE IF NOT EXISTS `reviews` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `product_id` INT NOT NULL,
    `rating` INT NOT NULL,
    `comment` TEXT,
    `status` VARCHAR(20) NOT NULL DEFAULT 'APPROVED',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NULL,
    `deleted_at` DATETIME NULL,
    `deletion_reason` VARCHAR(500) NULL,
    `deleted_by` VARCHAR(100) NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
    INDEX `idx_reviews_user_product` (`user_id`, `product_id`),
    INDEX `idx_reviews_product_status` (`product_id`, `status`),
    INDEX `idx_reviews_status` (`status`)
);

-- Create notifications table if it doesn't exist
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `message` TEXT NOT NULL,
    `type` VARCHAR(50) NOT NULL,
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `read_at` DATETIME NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_notifications_user` (`user_id`),
    INDEX `idx_notifications_read` (`is_read`),
    INDEX `idx_notifications_created` (`created_at`)
); 