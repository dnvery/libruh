package me.dnvery.libruh.service;

import me.dnvery.libruh.dto.book.BookListResponse;
import me.dnvery.libruh.dto.book.BookResponse;
import me.dnvery.libruh.dto.book.BookUpdateRequest;
import me.dnvery.libruh.entity.Book;
import me.dnvery.libruh.enums.ConversionStatus;
import me.dnvery.libruh.exception.FileUploadException;
import me.dnvery.libruh.exception.ResourceNotFoundException;
import me.dnvery.libruh.repository.BookRepository;
import me.dnvery.libruh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Value("${storage.dir:/app/storage}")
    private String storageDir;

    @Value("${storage.fb2-subdir:fb2}")
    private String fb2Subdir;

    @Value("${storage.epub-subdir:epub}")
    private String epubSubdir;

    @Value("${storage.azw8-subdir:azw8}")
    private String azw8Subdir;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public BookListResponse listBooks(int page, int size) {
        Page<Book> bookPage = bookRepository.findAllByOrderByUploadDateDesc(PageRequest.of(page, size));
        return toListResponse(bookPage);
    }

    public BookResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return toResponse(book);
    }

    public BookResponse uploadBook(MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".fb2")) {
            throw new FileUploadException("Only .fb2 files are accepted");
        }

        String uuid = UUID.randomUUID().toString();
        Path fb2Dir = Paths.get(storageDir, fb2Subdir);
        Path fb2Path = fb2Dir.resolve(uuid + ".fb2");

        try {
            Files.createDirectories(fb2Dir);
            Files.copy(file.getInputStream(), fb2Path);
        } catch (IOException e) {
            throw new FileUploadException("Failed to save file: " + e.getMessage());
        }

        Book book = new Book();
        book.setTitle(originalFilename.replaceAll("(?i)\\.fb2$", ""));
        book.setAuthor("Unknown");
        book.setFb2FilePath(fb2Path.toString());
        book.setConversionStatus(ConversionStatus.PENDING);
        book.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        book = bookRepository.save(book);
        return toResponse(book);
    }

    public BookResponse updateBook(Long id, BookUpdateRequest request, String username) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (!book.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only modify your own books");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setGenre(request.getGenre());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());

        if (request.getPublicationDate() != null && !request.getPublicationDate().isBlank()) {
            try {
                book.setPublicationDate(LocalDate.parse(request.getPublicationDate()));
            } catch (Exception ignored) {
            }
        }

        book = bookRepository.save(book);
        return toResponse(book);
    }

    public void deleteBook(Long id, String username) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (!book.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only delete your own books");
        }

        deleteFileIfExists(book.getFb2FilePath());
        deleteFileIfExists(book.getEpubFilePath());
        deleteFileIfExists(book.getAzw8FilePath());

        bookRepository.delete(book);
    }

    public BookListResponse searchBooks(String query, String field, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Book> bookPage;

        switch (field.toLowerCase()) {
            case "author":
                bookPage = bookRepository.findByAuthorContainingIgnoreCase(query, pageRequest);
                break;
            case "genre":
                bookPage = bookRepository.findByGenreContainingIgnoreCase(query, pageRequest);
                break;
            default:
                bookPage = bookRepository.findByTitleContainingIgnoreCase(query, pageRequest);
                break;
        }

        return toListResponse(bookPage);
    }

    public Resource downloadBook(Long id, String format) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (book.getConversionStatus() != ConversionStatus.COMPLETED) {
            throw new IllegalArgumentException("Book conversion is not completed. Status: " + book.getConversionStatus());
        }

        String filePath = format.equals("epub") ? book.getEpubFilePath() : book.getAzw8FilePath();

        if (filePath == null || filePath.isBlank()) {
            throw new ResourceNotFoundException(format.toUpperCase() + " file not found for book id: " + id);
        }

        try {
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found on disk: " + format.toUpperCase());
            }
            return resource;
        } catch (IOException e) {
            throw new ResourceNotFoundException("File not found: " + format.toUpperCase());
        }
    }

    public String getDownloadFilename(Long id, String format) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        String sanitizedTitle = book.getTitle().replaceAll("[^a-zA-Z0-9\\s\\-]", "").replaceAll("\\s+", "_");
        return sanitizedTitle + "." + format.toLowerCase();
    }

    private void deleteFileIfExists(String filePath) {
        if (filePath != null && !filePath.isBlank()) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException ignored) {
            }
        }
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getDescription(),
                book.getPublicationDate(),
                book.getLanguage(),
                book.getConversionStatus(),
                book.getUploadDate(),
                book.getUser().getId(),
                book.getUser().getUsername()
        );
    }

    private BookListResponse toListResponse(Page<Book> bookPage) {
        List<BookResponse> books = bookPage.getContent().stream().map(this::toResponse).toList();
        return new BookListResponse(
                books,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages()
        );
    }
}