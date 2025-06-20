# Library Management System

A robust, RESTful Library Management System built with Spring Boot, Hibernate, and JPA. This project allows you to manage books, handle borrowing transactions, and track borrowing history with a clean, professional codebase and user-friendly API.

---

## Features

- **Book Management:** Add, retrieve, and filter books by availability.
- **Borrowing Transactions:** Record, view, and return borrowed books.
- **Business Logic:** Enforces book availability, unique ISBN/title, and status updates.
- **Flexible Dates:** Accepts both `YYYY-MM-DD` and `YYYY-MM-DDTHH:mm:ss` formats.
- **Clear Error Handling:** Consistent, descriptive error messages for all API responses.
- **Clean Architecture:** DTOs, service layer, global exception handling, and modular packages.

---

## Technologies Used

- Java 21
- Spring Boot 3.5+
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven

---

## Getting Started

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd nyumbayirelaurent
```

### 2. Configure the Database

- Ensure PostgreSQL is running.
- Create a database named `library_db`.
- Update `src/main/resources/application.properties` with your DB credentials:
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
  spring.datasource.username=your_username
  spring.datasource.password=your_password
  ```

### 3. Build and Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The app will start on [http://localhost:8080](http://localhost:8080)

---

## API Endpoints

### Book APIs

- **Add a Book:**
  - `POST /api/library/books`
  - Body:
    ```json
    {
      "title": "Clean Code",
      "author": "Robert C. Martin",
      "isbn": "9780132350884",
      "availabilityStatus": "AVAILABLE"
    }
    ```
- **Get All Books:**
  - `GET /api/library/books`
- **Get Book by ISBN:**
  - `GET /api/library/books/{isbn}`
- **Get Books by Availability:**
  - `GET /api/library/books?availability=AVAILABLE`
- **Get Book Availability:**
  - `GET /api/library/books/{isbn}/availability`

### Borrowing Transaction APIs

- **Create a Borrowing Transaction:**
  - `POST /api/library/borrow`
  - Body:
    ```json
    {
      "isbn": "9780132350884",
      "borrowerName": "Alice",
      "borrowDate": "2024-06-08",
      "returnDate": "2024-06-15"
    }
    ```
- **Return a Book:**
  - `POST /api/library/return/{transactionId}`
- **Get All Transactions:**
  - `GET /api/library/transactions`

---

## Error Handling

- All errors return a clear JSON message, e.g.:
  ```json
  { "error": "A book with ISBN '123' already exists." }
  ```
- Handles invalid input, duplicates, not found, and business rule violations.

---

## Project Structure

```
NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent
│
├── controller      # REST API endpoints
├── dto             # Data Transfer Objects
├── entity          # JPA entities
├── exception       # Custom exceptions & global handler
├── repository      # Spring Data JPA repositories
├── service         # Business logic
└── NyumbayirelaurentApplication.java
```

---

## Contributing

1. Fork the repo
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a Pull Request

---

## License

This project is open source and available under the [MIT License](LICENSE).
