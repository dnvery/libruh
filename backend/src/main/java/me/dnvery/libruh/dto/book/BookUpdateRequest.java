package me.dnvery.libruh.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}