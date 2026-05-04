package me.dnvery.libruh.dto.book;

import java.util.List;

public class BookListResponse {
    private List<BookResponse> books;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public BookListResponse(List<BookResponse> books, int page, int size, long totalElements, int totalPages) {
        this.books = books;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<BookResponse> getBooks() { return books; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}