package me.dnvery.libruh.repository;

import me.dnvery.libruh.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findAllByOrderByUploadDateDesc(Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCase(String query, Pageable pageable);

    Page<Book> findByAuthorContainingIgnoreCase(String query, Pageable pageable);

    Page<Book> findByGenreContainingIgnoreCase(String query, Pageable pageable);

    List<Book> findByUserId(Long userId);
}