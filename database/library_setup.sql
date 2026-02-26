-- library_system.sql
CREATE DATABASE IF NOT EXISTS library_system;
USE library_system;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS Users (
    user_id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Librarian') NOT NULL
);

-- 2. Customers/Members Table
CREATE TABLE IF NOT EXISTS Customers (
    cust_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    contact VARCHAR(15),
    email VARCHAR(100)
);

-- 3. Books Table
CREATE TABLE IF NOT EXISTS Books (
    book_id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    qty INT NOT NULL DEFAULT 0
);

-- 4. Rentals Table
CREATE TABLE IF NOT EXISTS Rentals (
    rental_id VARCHAR(10) PRIMARY KEY,
    book_id VARCHAR(10),
    cust_id VARCHAR(10),
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE DEFAULT NULL,
    status ENUM('Pending', 'Returned', 'Overdue') DEFAULT 'Pending',
    fine DECIMAL(10, 2) DEFAULT 0.00,
    payment_status ENUM('Pending', 'Paid', 'N/A') DEFAULT 'N/A',
    FOREIGN KEY (book_id) REFERENCES Books(book_id),
    FOREIGN KEY (cust_id) REFERENCES Customers(cust_id)
);

-- 5. Settings Table
CREATE TABLE IF NOT EXISTS Settings (
    setting_id INT PRIMARY KEY,
    fine_per_day DECIMAL(10, 2) NOT NULL DEFAULT 15.00
);

INSERT INTO Settings (setting_id, fine_per_day) VALUES (1, 15.00);

-- 6. Live Rentals View (Handles Real-time Overdue/Fine Logic)
CREATE OR REPLACE VIEW Live_Rentals AS
SELECT 
    r.rental_id, c.name AS customer_name, b.title AS book_title,
    r.issue_date, r.due_date,
    CASE 
        WHEN r.return_date IS NULL AND CURRENT_DATE > r.due_date THEN 'Overdue'
        ELSE r.status 
    END AS live_status,
    CASE 
        WHEN r.return_date IS NULL AND CURRENT_DATE > r.due_date THEN 
            (DATEDIFF(CURRENT_DATE, r.due_date) * s.fine_per_day)
        ELSE r.fine 
    END AS live_fine
FROM Rentals r
JOIN Customers c ON r.cust_id = c.cust_id
JOIN Books b ON r.book_id = b.book_id
JOIN Settings s ON s.setting_id = 1;

-- 7. Trigger for Automatic Fine Calculation on Return
DELIMITER $$
CREATE TRIGGER Calculate_Fine_On_Return
BEFORE UPDATE ON Rentals
FOR EACH ROW
BEGIN
    IF NEW.return_date IS NOT NULL AND OLD.return_date IS NULL THEN
        SET NEW.status = 'Returned';
        IF NEW.return_date > NEW.due_date THEN
            SET NEW.fine = DATEDIFF(NEW.return_date, NEW.due_date) * (SELECT fine_per_day FROM Settings LIMIT 1);
            SET NEW.payment_status = 'Pending';
        ELSE
            SET NEW.fine = 0.00;
        END IF;
    END IF;
END $$
DELIMITER ;
