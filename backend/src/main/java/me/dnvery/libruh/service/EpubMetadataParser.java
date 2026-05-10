package me.dnvery.libruh.service;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class EpubMetadataParser {

    private static final Logger log = LoggerFactory.getLogger(EpubMetadataParser.class);

    public EpubMetadata parse(Path epubPath) {
        EpubMetadata metadata = new EpubMetadata();
        try (ZipFile zip = new ZipFile(epubPath.toFile())) {
            String containerXml = readZipEntry(zip, "META-INF/container.xml");
            if (containerXml == null) {
                log.warn("No META-INF/container.xml found in EPUB");
                return metadata;
            }

            String opfPath = parseContainerXml(containerXml);
            if (opfPath == null) {
                log.warn("Could not find OPF path in container.xml");
                return metadata;
            }

            log.debug("OPF path from container.xml: {}", opfPath);

            String opfXml = readZipEntry(zip, opfPath);
            if (opfXml == null) {
                log.warn("Could not read OPF file: {}", opfPath);
                return metadata;
            }

            log.debug("OPF content length: {} chars", opfXml.length());
            parseOpf(opfXml, metadata);

            log.info("EPUB metadata: title={}, author={}, publisher={}, language={}",
                    metadata.getTitle(), metadata.getAuthor(), metadata.getPublisher(), metadata.getLanguage());

        } catch (Exception e) {
            log.warn("Failed to parse EPUB metadata from {}: {}", epubPath, e.getMessage(), e);
        }
        return metadata;
    }

    private String parseContainerXml(String containerXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);

            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(containerXml.getBytes(StandardCharsets.UTF_8))));

            NodeList rootfiles = doc.getElementsByTagNameNS("urn:oasis:names:tc:opendocument:xmlns:container", "rootfile");
            for (int i = 0; i < rootfiles.getLength(); i++) {
                Element rootfile = (Element) rootfiles.item(i);
                String path = rootfile.getAttribute("full-path");
                if (path != null && !path.isEmpty()) {
                    return path;
                }
            }

            NodeList rootfilesNoNs = doc.getElementsByTagName("rootfile");
            for (int i = 0; i < rootfilesNoNs.getLength(); i++) {
                Element rootfile = (Element) rootfilesNoNs.item(i);
                String path = rootfile.getAttribute("full-path");
                if (path != null && !path.isEmpty()) {
                    return path;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse container.xml: {}", e.getMessage());
        }
        return null;
    }

    private void parseOpf(String opfXml, EpubMetadata metadata) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);

            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(opfXml.getBytes(StandardCharsets.UTF_8))));

            Element metadataEl = findElementByNs(doc, "http://www.idpf.org/2007/opf", "metadata");
            if (metadataEl == null) {
                metadataEl = findElementByLocalName(doc, "metadata");
            }
            if (metadataEl == null) {
                log.warn("No <metadata> element found in OPF");
                return;
            }

            String dcNs = "http://purl.org/dc/elements/1.1/";

            List<String> titles = getAllTextsNs(metadataEl, dcNs, "title");
            if (titles.isEmpty()) titles = getAllTextsByTag(metadataEl, "dc:title");
            if (titles.isEmpty()) titles = getAllTextsByTag(metadataEl, "title");
            if (!titles.isEmpty()) metadata.setTitle(String.join(", ", titles));

            List<String> creators = getAllTextsNs(metadataEl, dcNs, "creator");
            if (creators.isEmpty()) creators = getAllTextsByTag(metadataEl, "dc:creator");
            if (creators.isEmpty()) creators = getAllTextsByTag(metadataEl, "creator");
            if (!creators.isEmpty()) metadata.setAuthor(String.join(", ", creators));

            List<String> publishers = getAllTextsNs(metadataEl, dcNs, "publisher");
            if (publishers.isEmpty()) publishers = getAllTextsByTag(metadataEl, "dc:publisher");
            if (publishers.isEmpty()) publishers = getAllTextsByTag(metadataEl, "publisher");
            if (!publishers.isEmpty()) metadata.setPublisher(String.join(", ", publishers));

            List<String> languages = getAllTextsNs(metadataEl, dcNs, "language");
            if (languages.isEmpty()) languages = getAllTextsByTag(metadataEl, "dc:language");
            if (languages.isEmpty()) languages = getAllTextsByTag(metadataEl, "language");
            if (!languages.isEmpty()) metadata.setLanguage(languages.get(0));

            List<String> identifiers = getAllTextsNs(metadataEl, dcNs, "identifier");
            if (identifiers.isEmpty()) identifiers = getAllTextsByTag(metadataEl, "dc:identifier");
            if (identifiers.isEmpty()) identifiers = getAllTextsByTag(metadataEl, "identifier");
            if (!identifiers.isEmpty()) metadata.setIdentifier(identifiers.get(0));

            List<String> descriptions = getAllTextsNs(metadataEl, dcNs, "description");
            if (descriptions.isEmpty()) descriptions = getAllTextsByTag(metadataEl, "dc:description");
            if (descriptions.isEmpty()) descriptions = getAllTextsByTag(metadataEl, "description");
            if (!descriptions.isEmpty()) metadata.setDescription(descriptions.get(0));

            List<String> dates = getAllTextsNs(metadataEl, dcNs, "date");
            if (dates.isEmpty()) dates = getAllTextsByTag(metadataEl, "dc:date");
            if (dates.isEmpty()) dates = getAllTextsByTag(metadataEl, "date");
            if (!dates.isEmpty()) metadata.setPublicationDate(dates.get(0));

            List<String> subjects = getAllTextsNs(metadataEl, dcNs, "subject");
            if (subjects.isEmpty()) subjects = getAllTextsByTag(metadataEl, "dc:subject");
            if (subjects.isEmpty()) subjects = getAllTextsByTag(metadataEl, "subject");
            if (!subjects.isEmpty()) metadata.setSubjects(subjects);

        } catch (Exception e) {
            log.warn("Failed to parse OPF: {}", e.getMessage(), e);
        }
    }

    private String readZipEntry(ZipFile zip, String entryPath) {
        try {
            ZipEntry entry = zip.getEntry(entryPath);
            if (entry == null) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry e = entries.nextElement();
                    if (e.getName().equalsIgnoreCase(entryPath)) {
                        entry = e;
                        break;
                    }
                }
            }
            if (entry == null) {
                return null;
            }
            return new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Failed to read zip entry {}: {}", entryPath, e.getMessage());
            return null;
        }
    }

    private Element findElementByNs(Document doc, String namespaceUri, String localName) {
        NodeList nodes = doc.getElementsByTagNameNS(namespaceUri, localName);
        if (nodes.getLength() > 0) {
            return (Element) nodes.item(0);
        }
        return null;
    }

    private Element findElementByLocalName(Document doc, String localName) {
        NodeList allElements = doc.getElementsByTagName("*");
        for (int i = 0; i < allElements.getLength(); i++) {
            Element el = (Element) allElements.item(i);
            if (localName.equals(el.getLocalName()) || localName.equals(el.getTagName())) {
                return el;
            }
        }
        return null;
    }

    private List<String> getAllTextsNs(Element parent, String ns, String localName) {
        List<String> result = new ArrayList<>();
        NodeList nodes = parent.getElementsByTagNameNS(ns, localName);
        for (int i = 0; i < nodes.getLength(); i++) {
            String text = nodes.item(i).getTextContent();
            if (text != null && !text.isBlank()) {
                result.add(text.trim());
            }
        }
        return result;
    }

    private List<String> getAllTextsByTag(Element parent, String tagName) {
        List<String> result = new ArrayList<>();
        NodeList nodes = parent.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            String text = nodes.item(i).getTextContent();
            if (text != null && !text.isBlank()) {
                result.add(text.trim());
            }
        }
        return result;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EpubMetadata {
        private String title;
        private String author;
        private String publisher;
        private String language;
        private String identifier;
        private String description;
        private String publicationDate;
        private List<String> subjects = new ArrayList<>();
    }
}