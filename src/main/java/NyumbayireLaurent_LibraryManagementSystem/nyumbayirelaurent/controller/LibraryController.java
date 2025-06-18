package NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.controller;

import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.dto.BookDTO;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.dto.BorrowingTransactionDTO;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.entity.Book;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.exception.BadRequestException;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    @Autowired
    private LibraryService libraryService;

    // 1. Create a new book
    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(@RequestBody Map<String, String> body) {
        BookDTO book = libraryService.createBook(
                body.get("title"),
                body.get("author"),
                body.get("isbn"),
                Book.AvailabilityStatus.valueOf(body.get("availabilityStatus")));
        return ResponseEntity.ok(book);
    }

    // 2. Retrieve book details by ISBN
    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
        BookDTO book = libraryService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    // 3. Create a new borrowing transaction (accepts both date formats)
    @PostMapping("/borrow")
    public ResponseEntity<BorrowingTransactionDTO> createBorrowingTransaction(@RequestBody Map<String, String> body) {
        String dateStr = body.get("borrowDate");
        LocalDateTime borrowDate;
        try {
            if (dateStr.length() == 10) { // YYYY-MM-DD
                borrowDate = LocalDate.parse(dateStr).atStartOfDay();
            } else {
                borrowDate = LocalDateTime.parse(dateStr);
            }
        } catch (Exception e) {
            throw new BadRequestException("Invalid date format. Use YYYY-MM-DD or YYYY-MM-DDTHH:mm:ss");
        }
        BorrowingTransactionDTO transaction = libraryService.createBorrowingTransaction(
                body.get("isbn"),
                body.get("borrowerName"),
                borrowDate);
        return ResponseEntity.ok(transaction);
    }

    // 4. Retrieve book availability
    @GetMapping("/books/{isbn}/availability")
    public ResponseEntity<?> getBookAvailability(@PathVariable String isbn) {
        String status = libraryService.getBookAvailability(isbn);
        return ResponseEntity.ok(Map.of("availability", status));
    }

    // 5. Return a borrowed book
    @PostMapping("/return/{transactionId}")
    public ResponseEntity<BorrowingTransactionDTO> returnBook(@PathVariable Long transactionId) {
        BorrowingTransactionDTO transaction = libraryService.returnBook(transactionId);
        return ResponseEntity.ok(transaction);
    }

    // 6. Get all books, with optional availability filter
    @GetMapping("/books")
    public ResponseEntity<?> getAllBooks(@RequestParam(value = "availability", required = false) String availability) {
        if (availability != null) {
            return ResponseEntity.ok(libraryService.getBooksByAvailability(availability));
        }
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    // 7. Get all borrowing transactions
    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(libraryService.getAllTransactions());
    }
}