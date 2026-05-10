package me.dnvery.libruh.dto.book;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookListResponse {
    private List<BookResponse> books;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}