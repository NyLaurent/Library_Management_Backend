package NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.repository;

import NyumbayireLaurent_LibraryManagementSystem.nyumbayirelaurent.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    Optional<Book> findByTitle(String title);
} 