package me.dnvery.libruh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class Fb2MetadataParser {

    private static final Logger log = LoggerFactory.getLogger(Fb2MetadataParser.class);

    public ParsedMetadata parse(String fb2Content) {
        ParsedMetadata metadata = new ParsedMetadata();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setXIncludeAware(false);

            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(fb2Content)));

            Element titleInfo = getFirstElementByTagPath(doc.getDocumentElement(), "description", "title-info");
            if (titleInfo == null) {
                titleInfo = getFirstElement(doc.getDocumentElement(), "title-info");
            }

            if (titleInfo != null) {
                metadata.setTitle(getText(titleInfo, "book-title"));
                metadata.setGenre(formatGenres(titleInfo));
                metadata.setLanguage(getText(titleInfo, "lang"));

                String dateText = getText(titleInfo, "date");
                if (dateText != null && !dateText.isBlank()) {
                    try {
                        metadata.setPublicationDate(LocalDate.parse(dateText + "-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } catch (Exception e1) {
                        try {
                            metadata.setPublicationDate(LocalDate.parse(dateText));
                        } catch (Exception e2) {
                            log.debug("Could not parse date: {}", dateText);
                        }
                    }
                }

                Element authorEl = getFirstElement(titleInfo, "author");
                if (authorEl != null) {
                    String firstName = getText(authorEl, "first-name");
                    String middleName = getText(authorEl, "middle-name");
                    String lastName = getText(authorEl, "last-name");
                    metadata.setAuthor(formatAuthor(firstName, middleName, lastName));
                }

                Element annotation = getFirstElement(titleInfo, "annotation");
                if (annotation != null) {
                    metadata.setDescription(extractTextContent(annotation));
                }
            }

            metadata.setCoverImage(extractCoverImage(doc));

            if (metadata.getTitle() == null || metadata.getTitle().isBlank()) {
                metadata.setTitle("Unknown Title");
            }
            if (metadata.getAuthor() == null || metadata.getAuthor().isBlank()) {
                metadata.setAuthor("Unknown Author");
            }

        } catch (Exception e) {
            log.warn("Failed to parse FB2 metadata: {}", e.getMessage());
        }

        return metadata;
    }

    private String formatAuthor(String firstName, String middleName, String lastName) {
        List<String> parts = new ArrayList<>();
        if (lastName != null && !lastName.isBlank()) parts.add(lastName);
        if (firstName != null && !firstName.isBlank()) parts.add(firstName);
        if (middleName != null && !middleName.isBlank()) parts.add(middleName);
        return String.join(" ", parts);
    }

    private Element getFirstElement(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return (Element) nodes.item(0);
        }
        return null;
    }

    private Element getFirstElementByTagPath(Element root, String... tagNames) {
        Element current = root;
        for (String tag : tagNames) {
            NodeList nodes = current.getElementsByTagName(tag);
            if (nodes.getLength() > 0) {
                current = (Element) nodes.item(0);
            } else {
                return null;
            }
        }
        return current;
    }

    private String getText(Element parent, String tagName) {
        Element el = getFirstElement(parent, tagName);
        if (el != null) {
            String text = el.getTextContent();
            return text != null ? text.trim() : null;
        }
        return null;
    }

    private String extractTextContent(Element element) {
        StringBuilder sb = new StringBuilder();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            String text = children.item(i).getTextContent();
            if (text != null && !text.isBlank()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(text.trim());
            }
        }
        return sb.toString().trim();
    }

    private String formatGenres(Element titleInfo) {
        NodeList genreNodes = titleInfo.getElementsByTagName("genre");
        if (genreNodes.getLength() == 0) return null;

        List<String> genres = new ArrayList<>();
        for (int i = 0; i < genreNodes.getLength(); i++) {
            String raw = genreNodes.item(i).getTextContent();
            if (raw != null && !raw.isBlank()) {
                genres.add(formatGenreCode(raw.trim()));
            }
        }
        return genres.isEmpty() ? null : String.join(", ", genres);
    }

    private String formatGenreCode(String code) {
        String readable = code.replace('_', ' ');
        readable = readable.replaceAll("\\s+\\d+$", "");
        if (readable.isEmpty()) return code;
        return Character.toUpperCase(readable.charAt(0)) + readable.substring(1);
    }

    private CoverImage extractCoverImage(Document doc) {
        String imageId = null;

        Element titleInfo = getFirstElementByTagPath(doc.getDocumentElement(), "description", "title-info");
        if (titleInfo == null) {
            titleInfo = getFirstElement(doc.getDocumentElement(), "title-info");
        }

        if (titleInfo != null) {
            Element coverpage = getFirstElement(titleInfo, "coverpage");
            if (coverpage != null) {
                NodeList images = coverpage.getElementsByTagName("image");
                for (int i = 0; i < images.getLength(); i++) {
                    Element image = (Element) images.item(i);
                    String href = image.getAttributeNS("http://www.w3.org/1999/xlink", "href");
                    if (href == null || href.isEmpty()) {
                        href = image.getAttribute("l:href");
                    }
                    if (href == null || href.isEmpty()) {
                        href = image.getAttribute("href");
                    }
                    if (href != null && !href.isEmpty() && href.startsWith("#")) {
                        imageId = href.substring(1);
                        log.debug("Found cover image reference: id={}", imageId);
                        break;
                    }
                }
            }
        }

        NodeList binaries = doc.getElementsByTagName("binary");
        for (int i = 0; i < binaries.getLength(); i++) {
            Element binary = (Element) binaries.item(i);
            String id = binary.getAttribute("id");

            if (imageId != null && !imageId.equals(id)) {
                continue;
            }

            String contentType = binary.getAttribute("content-type");
            if (contentType == null || contentType.isEmpty()) {
                contentType = guessContentType(id);
            }

            if (contentType != null && contentType.startsWith("image/")) {
                String base64Data = binary.getTextContent().replaceAll("\\s+", "");
                if (!base64Data.isEmpty()) {
                    try {
                        byte[] imageBytes = Base64.getMimeDecoder().decode(base64Data);
                        String extension = extensionFromContentType(contentType);
                        log.info("Extracted cover image: id={}, type={}, size={} bytes", id, contentType, imageBytes.length);
                        return new CoverImage(imageBytes, contentType, extension);
                    } catch (IllegalArgumentException e) {
                        log.warn("Failed to decode cover image binary for id={}: {}", id, e.getMessage());
                    }
                }
            }
        }

        if (imageId == null) {
            NodeList allBinaries = doc.getElementsByTagName("binary");
            for (int i = 0; i < allBinaries.getLength(); i++) {
                Element binary = (Element) allBinaries.item(i);
                String id = binary.getAttribute("id");
                String contentType = binary.getAttribute("content-type");
                if (contentType != null && contentType.startsWith("image/")) {
                    if (id != null && (id.toLowerCase().contains("cover") || id.toLowerCase().contains("img"))) {
                        String base64Data = binary.getTextContent().replaceAll("\\s+", "");
                        if (!base64Data.isEmpty()) {
                            try {
                                byte[] imageBytes = Base64.getMimeDecoder().decode(base64Data);
                                String extension = extensionFromContentType(contentType);
                                return new CoverImage(imageBytes, contentType, extension);
                            } catch (IllegalArgumentException e) {
                                log.warn("Failed to decode cover image binary for id={}: {}", id, e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        log.debug("No cover image found in FB2");
        return null;
    }

    private String guessContentType(String id) {
        if (id == null) return null;
        String lower = id.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        return null;
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) return "jpg";
        if ("image/png".equals(contentType)) return "png";
        if ("image/gif".equals(contentType)) return "gif";
        if ("image/webp".equals(contentType)) return "webp";
        return "jpg";
    }

    public static class ParsedMetadata {
        private String title;
        private String author;
        private String genre;
        private String description;
        private LocalDate publicationDate;
        private String language;
        private CoverImage coverImage;

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
        public CoverImage getCoverImage() { return coverImage; }
        public void setCoverImage(CoverImage coverImage) { this.coverImage = coverImage; }
    }

    public static class CoverImage {
        private final byte[] data;
        private final String contentType;
        private final String extension;

        public CoverImage(byte[] data, String contentType, String extension) {
            this.data = data;
            this.contentType = contentType;
            this.extension = extension;
        }

        public byte[] getData() { return data; }
        public String getContentType() { return contentType; }
        public String getExtension() { return extension; }
    }
}