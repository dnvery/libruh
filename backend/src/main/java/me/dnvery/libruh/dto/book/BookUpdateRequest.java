package me.dnvery.libruh.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class BookUpdateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be at most 500 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 500, message = "Author must be at most 500 characters")
    private String author;

    @Size(max = 255, message = "Genre must be at most 255 characters")
    private String genre;

    private String description;

    private String publicationDate;

    @Size(max = 10, message = "Language must be at most 10 characters")
    private String language;

    @Size(max = 255, message = "Sequence name must be at most 255 characters")
    private String sequenceName;

    @Size(max = 10, message = "Sequence number must be at most 10 characters")
    private String sequenceNumber;
}