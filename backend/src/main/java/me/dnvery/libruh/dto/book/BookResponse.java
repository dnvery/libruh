package me.dnvery.libruh.dto.book;

import me.dnvery.libruh.enums.ConversionStatus;

import java.time.Instant;
import java.time.LocalDate;

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private LocalDate publicationDate;
    private String language;
    private ConversionStatus conversionStatus;
    private Instant uploadDate;
    private Long userId;
    private String username;
    private boolean hasCover;

    public BookResponse(Long id, String title, String author, String genre, String description,
                        LocalDate publicationDate, String language, ConversionStatus conversionStatus,
                        Instant uploadDate, Long userId, String username, boolean hasCover) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.publicationDate = publicationDate;
        this.language = language;
        this.conversionStatus = conversionStatus;
        this.uploadDate = uploadDate;
        this.userId = userId;
        this.username = username;
        this.hasCover = hasCover;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public String getLanguage() { return language; }
    public ConversionStatus getConversionStatus() { return conversionStatus; }
    public Instant getUploadDate() { return uploadDate; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public boolean isHasCover() { return hasCover; }
}