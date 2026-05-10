package me.dnvery.libruh.entity;

import jakarta.persistence.*;
import lombok.*;
import me.dnvery.libruh.enums.ConversionStatus;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}