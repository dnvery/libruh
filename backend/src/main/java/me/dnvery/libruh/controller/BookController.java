package me.dnvery.libruh.controller;

import me.dnvery.libruh.dto.book.BookListResponse;
import me.dnvery.libruh.dto.book.BookResponse;
import me.dnvery.libruh.dto.book.BookUpdateRequest;
import me.dnvery.libruh.dto.book.ConversionDetailResponse;
import me.dnvery.libruh.service.BookService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<BookListResponse> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookService.listBooks(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    @PostMapping
    public ResponseEntity<BookResponse> uploadBook(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(bookService.uploadBook(file, username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(bookService.updateBook(id, request, username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        bookService.deleteBook(id, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reconvert")
    public ResponseEntity<Void> reconvertBook(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        bookService.reconvertBook(id, username);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/search")
    public ResponseEntity<BookListResponse> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String field,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookService.searchBooks(query, field, page, size));
    }

    @GetMapping("/{id}/epub")
    public ResponseEntity<Resource> streamEpub(@PathVariable Long id) {
        Resource resource = bookService.downloadBook(id, "epub");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/epub+zip"))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(resource);
    }

    @GetMapping("/{id}/download/epub")
    public ResponseEntity<Resource> downloadEpub(@PathVariable Long id) {
        Resource resource = bookService.downloadBook(id, "epub");
        String filename = bookService.getDownloadFilename(id, "epub");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/download/azw8")
    public ResponseEntity<Resource> downloadAzw8(@PathVariable Long id) {
        Resource resource = bookService.downloadBook(id, "azw8");
        String filename = bookService.getDownloadFilename(id, "azw8");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> getCoverImage(@PathVariable Long id) {
        BookService.CoverImageData cover = bookService.getCoverImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(cover.contentType()))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(cover.resource());
    }

    @GetMapping("/{id}/conversion")
    public ResponseEntity<ConversionDetailResponse> getConversionDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getConversionDetail(id));
    }
}