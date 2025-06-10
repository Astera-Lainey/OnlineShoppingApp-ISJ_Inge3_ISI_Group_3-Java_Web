-- Check if reviews table exists
SHOW TABLES LIKE 'reviews';

-- Check table structure
DESCRIBE reviews;

-- Check if there are any reviews in the table
SELECT COUNT(*) as review_count FROM reviews;

-- Check sample reviews
SELECT * FROM reviews LIMIT 5;

-- Check review status distribution
SELECT status, COUNT(*) as count FROM reviews GROUP BY status; 