package me.dnvery.libruh.entity;

import jakarta.persistence.*;
import me.dnvery.libruh.enums.ConversionStatus;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 500)
    private String author;

    @Column(length = 255)
    private String genre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(length = 10)
    private String language;

    @Column(name = "sequence_name", length = 255)
    private String sequenceName;

    @Column(name = "sequence_number", length = 10)
    private String sequenceNumber;

    @Column(name = "fb2_file_path", nullable = false, length = 1000)
    private String fb2FilePath;

    @Column(name = "epub_file_path", length = 1000)
    private String epubFilePath;

    @Column(name = "azw8_file_path", length = 1000)
    private String azw8FilePath;

    @Column(name = "cover_image_path", length = 1000)
    private String coverImagePath;

    @Column(name = "epub_file_size")
    private Long epubFileSize;

    @Column(name = "azw8_file_size")
    private Long azw8FileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversion_status", nullable = false, length = 20)
    private ConversionStatus conversionStatus = ConversionStatus.PENDING;

    @Column(name = "upload_date", nullable = false, updatable = false)
    private Instant uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        uploadDate = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getSequenceName() { return sequenceName; }
    public void setSequenceName(String sequenceName) { this.sequenceName = sequenceName; }
    public String getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(String sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public String getFb2FilePath() { return fb2FilePath; }
    public void setFb2FilePath(String fb2FilePath) { this.fb2FilePath = fb2FilePath; }
    public String getEpubFilePath() { return epubFilePath; }
    public void setEpubFilePath(String epubFilePath) { this.epubFilePath = epubFilePath; }
    public String getAzw8FilePath() { return azw8FilePath; }
    public void setAzw8FilePath(String azw8FilePath) { this.azw8FilePath = azw8FilePath; }
    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }
    public Long getEpubFileSize() { return epubFileSize; }
    public void setEpubFileSize(Long epubFileSize) { this.epubFileSize = epubFileSize; }
    public Long getAzw8FileSize() { return azw8FileSize; }
    public void setAzw8FileSize(Long azw8FileSize) { this.azw8FileSize = azw8FileSize; }
    public ConversionStatus getConversionStatus() { return conversionStatus; }
    public void setConversionStatus(ConversionStatus conversionStatus) { this.conversionStatus = conversionStatus; }
    public Instant getUploadDate() { return uploadDate; }
    public void setUploadDate(Instant uploadDate) { this.uploadDate = uploadDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}