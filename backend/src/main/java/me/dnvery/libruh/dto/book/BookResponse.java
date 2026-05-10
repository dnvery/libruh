package me.dnvery.libruh.dto.book;

import lombok.*;
import me.dnvery.libruh.enums.ConversionStatus;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Long epubFileSize;
    private Long azw8FileSize;
    private String sequenceName;
    private String sequenceNumber;
}