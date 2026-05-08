package me.dnvery.libruh.dto.book;

import java.util.List;

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

    public ConversionDetailResponse(Long id, String conversionStatus, Long epubFileSize, Long azw8FileSize,
                                     String epubTitle, String epubAuthor, String epubPublisher,
                                     String epubLanguage, String epubIdentifier, String epubPublicationDate,
                                     List<String> epubSubjects) {
        this.id = id;
        this.conversionStatus = conversionStatus;
        this.epubFileSize = epubFileSize;
        this.azw8FileSize = azw8FileSize;
        this.epubTitle = epubTitle;
        this.epubAuthor = epubAuthor;
        this.epubPublisher = epubPublisher;
        this.epubLanguage = epubLanguage;
        this.epubIdentifier = epubIdentifier;
        this.epubPublicationDate = epubPublicationDate;
        this.epubSubjects = epubSubjects;
    }

    public Long getId() { return id; }
    public String getConversionStatus() { return conversionStatus; }
    public Long getEpubFileSize() { return epubFileSize; }
    public Long getAzw8FileSize() { return azw8FileSize; }
    public String getEpubTitle() { return epubTitle; }
    public String getEpubAuthor() { return epubAuthor; }
    public String getEpubPublisher() { return epubPublisher; }
    public String getEpubLanguage() { return epubLanguage; }
    public String getEpubIdentifier() { return epubIdentifier; }
    public String getEpubPublicationDate() { return epubPublicationDate; }
    public List<String> getEpubSubjects() { return epubSubjects; }
}