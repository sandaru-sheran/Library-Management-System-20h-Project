# 📚 Library Management System (JavaFX)

A fully functional, standalone desktop application built with JavaFX and MySQL, designed to streamline and automate real-world library operations. This project serves as the final coursework submission for the ICET JavaFX Application Development module.

## ✨ Key Features

* **Secure Authentication:** Role-based access control directing users to dedicated Administrator or Librarian (Staff) dashboards.
* **Dynamic Database Configuration:** Includes a robust fallback UI that intercepts database connection failures and prompts the user to configure MySQL credentials dynamically.
* **Book & Inventory Management:** Full CRUD capabilities to manage book titles, authors, categories, and real-time stock quantities.
* **Customer Directory:** Maintain detailed records of library members and their contact information.
* **Rental Operations & Fine Tracking:** * Issue and return books with automated stock updates.
    * Integrated MySQL Views and Triggers to dynamically calculate overdue days and pending fines in real-time.
    * Process fine payments directly through the UI.
* **System Analytics & Reporting:** * High-level dashboard metrics (Total Users, Unreturned Books, Revenue).
    * Embedded visual analytics (Pie charts for inventory distribution).
    * **Jasper Reports Integration:** Export comprehensive, professional PDF rental logs and financial reports.
* **Modern UI/UX:** Designed with a sleek, consistent dark theme utilizing custom CSS and borderless confirmation dialogs.

## 🛠️ Technology Stack

* **Language:** Java 
* **GUI Framework:** JavaFX (FXML & CSS)
* **Database:** MySQL
* **Reporting:** JasperReports
* **Architecture:** Object-Oriented Design with layered controllers and model (TM) data transfer objects.

## 🚀 Setup & Installation

1. **Database Setup:** * Ensure MySQL server is running.
   * Execute the provided `library_system.sql` script (located in the `/database` folder) to generate the schema, tables, views, and triggers.
2. **Dependencies:**
   * Add the MySQL Connector/J library to your project structure.
   * Add the JasperReports core and dependency JARs.
3. **Execution:**
   * Run the application. If the default database credentials fail, the system will automatically open the `DB Configuration` window to allow you to input your specific host, port, username, and password.

## 👨‍💻 Author
**Sandaru Sheran** ```

***
