package NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.service;

import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.dto.BookDTO;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.dto.BorrowingTransactionDTO;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.entity.Book;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.entity.BorrowingTransaction;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.exception.BadRequestException;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.exception.ResourceNotFoundException;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.repository.BookRepository;
import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.repository.BorrowingTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowingTransactionRepository transactionRepository;

    public BookDTO createBook(String title, String author, String isbn, Book.AvailabilityStatus status) {
        if (bookRepository.findByIsbn(isbn).isPresent()) {
            throw new BadRequestException("A book with ISBN '" + isbn + "' already exists.");
        }
        if (bookRepository.findByTitle(title).isPresent()) {
            throw new BadRequestException("A book with title '" + title + "' already exists.");
        }
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setAvailabilityStatus(status);
        Book saved = bookRepository.save(book);
        return toBookDTO(saved);
    }

    public BookDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
        return toBookDTO(book);
    }

    public String getBookAvailability(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
        return book.getAvailabilityStatus().name();
    }

    @Transactional
    public BorrowingTransactionDTO createBorrowingTransaction(String isbn, String borrowerName,
            LocalDateTime borrowDate, LocalDateTime returnDate) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
        if (book.getAvailabilityStatus() != Book.AvailabilityStatus.AVAILABLE) {
            throw new BadRequestException("Book is not available for borrowing");
        }
        book.setAvailabilityStatus(Book.AvailabilityStatus.BORROWED);
        bookRepository.save(book);

        BorrowingTransaction transaction = new BorrowingTransaction();
        transaction.setBook(book);
        transaction.setBorrowerName(borrowerName);
        transaction.setBorrowDate(borrowDate);
        transaction.setReturnDate(returnDate);
        transaction.setStatus(BorrowingTransaction.Status.PENDING);
        BorrowingTransaction saved = transactionRepository.save(transaction);
        return toBorrowingTransactionDTO(saved);
    }

    @Transactional
    public BorrowingTransactionDTO returnBook(Long transactionId) {
        BorrowingTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
        if (transaction.getStatus() == BorrowingTransaction.Status.RETURNED) {
            throw new BadRequestException("Book already returned");
        }
        transaction.setStatus(BorrowingTransaction.Status.RETURNED);
        transaction.setReturnDate(LocalDateTime.now());
        Book book = transaction.getBook();
        book.setAvailabilityStatus(Book.AvailabilityStatus.AVAILABLE);
        bookRepository.save(book);
        BorrowingTransaction saved = transactionRepository.save(transaction);
        return toBorrowingTransactionDTO(saved);
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(this::toBookDTO).toList();
    }

    public List<BookDTO> getBooksByAvailability(String availability) {
        Book.AvailabilityStatus status;
        try {
            status = Book.AvailabilityStatus.valueOf(availability.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid availability status. Use AVAILABLE or BORROWED.");
        }
        return bookRepository.findAll().stream()
                .filter(book -> book.getAvailabilityStatus() == status)
                .map(this::toBookDTO)
                .toList();
    }

    public List<BorrowingTransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream().map(this::toBorrowingTransactionDTO).toList();
    }

    // --- DTO Mappers ---
    private BookDTO toBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setAvailabilityStatus(book.getAvailabilityStatus().name());
        return dto;
    }

    private BorrowingTransactionDTO toBorrowingTransactionDTO(BorrowingTransaction transaction) {
        BorrowingTransactionDTO dto = new BorrowingTransactionDTO();
        dto.setId(transaction.getId());
        dto.setBookIsbn(transaction.getBook().getIsbn());
        dto.setBookTitle(transaction.getBook().getTitle());
        dto.setBorrowerName(transaction.getBorrowerName());
        dto.setBorrowDate(transaction.getBorrowDate() != null ? transaction.getBorrowDate().toString() : null);
        dto.setReturnDate(transaction.getReturnDate() != null ? transaction.getReturnDate().toString() : null);
        dto.setStatus(transaction.getStatus().name());
        return dto;
    }
}