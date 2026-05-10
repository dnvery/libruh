package me.dnvery.libruh.dto.book;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversionDetailResponse {
    private Long id;
    private String conversionStatus;
    private Long epubFileSize;
    private Long azw8FileSize;
    private String epubTitle;
    private String epubAuthor;
    private String epubPublisher;
    private String epubLanguage;
    private String epubIdentifier;
    private String epubPublicationDate;
    private List<String> epubSubjects;
}