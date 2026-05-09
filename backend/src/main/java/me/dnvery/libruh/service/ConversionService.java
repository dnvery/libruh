package me.dnvery.libruh.service;

import me.dnvery.libruh.dto.config.Fb2cngConfig;
import me.dnvery.libruh.entity.Book;
import me.dnvery.libruh.enums.ConversionStatus;
import me.dnvery.libruh.exception.ConversionException;
import me.dnvery.libruh.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ConversionService {

    private static final Logger log = LoggerFactory.getLogger(ConversionService.class);

    private final BookRepository bookRepository;
    private final ConversionConfigService configService;

    @Value("${fbc.path:/usr/local/bin/fbc}")
    private String fbcPath;

    @Value("${storage.dir:/app/storage}")
    private String storageDir;

    @Value("${storage.epub-subdir:epub}")
    private String epubSubdir;

    @Value("${storage.azw8-subdir:azw8}")
    private String azw8Subdir;

    public ConversionService(BookRepository bookRepository, ConversionConfigService configService) {
        this.bookRepository = bookRepository;
        this.configService = configService;
    }

    @Async
    public void convertAsync(Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            log.error("Book not found for conversion: {}", bookId);
            return;
        }

        book.setConversionStatus(ConversionStatus.PROCESSING);
        bookRepository.save(book);

        Path fb2Path = Paths.get(book.getFb2FilePath());
        String uuid = extractUuid(fb2Path);
        Path epubDir = Paths.get(storageDir, epubSubdir);
        Path azw8Dir = Paths.get(storageDir, azw8Subdir);

        Path configFile = null;
        try {
            Fb2cngConfig config = configService.getConfig();
            configFile = configService.writeConfigToTempFile(config);
            log.info("Config written to temp file: {}", configFile);

            Files.createDirectories(epubDir);
            Files.createDirectories(azw8Dir);

            log.info("Starting conversion for book {} (uuid={})", bookId, uuid);

            Process epubProcess = runFbc(configFile, "--to", "epub3", fb2Path.toString(), epubDir.toString());
            Process azw8Process = runFbc(configFile, "--to", "azw8", fb2Path.toString(), azw8Dir.toString());

            int epubExit = epubProcess.waitFor();
            String epubLog = readOutput(epubProcess);
            int azw8Exit = azw8Process.waitFor();
            String azw8Log = readOutput(azw8Process);

            if (epubExit != 0) {
                log.error("EPUB conversion failed (exit={}): {}", epubExit, epubLog);
                throw new ConversionException("EPUB conversion failed");
            }
            if (azw8Exit != 0) {
                log.error("AZW8 conversion failed (exit={}): {}", azw8Exit, azw8Log);
                throw new ConversionException("AZW8 conversion failed");
            }

            Path epubFile = findConvertedFile(epubDir, uuid, ".epub");
            Path azw8File = findConvertedFile(azw8Dir, uuid, ".azw8");

            if (epubFile != null) {
                Path renamedEpub = epubDir.resolve(uuid + ".epub");
                Files.move(epubFile, renamedEpub);
                book.setEpubFilePath(renamedEpub.toString());
                book.setEpubFileSize(Files.size(renamedEpub));
                log.info("EPUB converted: {} ({} bytes)", renamedEpub, book.getEpubFileSize());
            }

            if (azw8File != null) {
                Path renamedAzw8 = azw8Dir.resolve(uuid + ".azw8");
                Files.move(azw8File, renamedAzw8);
                book.setAzw8FilePath(renamedAzw8.toString());
                book.setAzw8FileSize(Files.size(renamedAzw8));
                log.info("AZW8 converted: {} ({} bytes)", renamedAzw8, book.getAzw8FileSize());
            }

            book.setConversionStatus(ConversionStatus.COMPLETED);
            log.info("Conversion completed for book {}", bookId);

        } catch (ConversionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Conversion failed for book {}: {}", bookId, e.getMessage(), e);
            book.setConversionStatus(ConversionStatus.FAILED);
        } finally {
            if (configFile != null) {
                try {
                    Files.deleteIfExists(configFile);
                } catch (IOException e) {
                    log.warn("Failed to delete temp config file: {}", configFile, e);
                }
            }
        }

        bookRepository.save(book);
    }

    private Process runFbc(Path configFile, String... args) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(fbcPath);
        command.add("convert");
        command.add("--config");
        command.add(configFile.toString());
        command.addAll(Arrays.asList(args));
        command.add("--overwrite");

        log.info("Running: {}", String.join(" ", command));

        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }

    private String extractUuid(Path fb2Path) {
        String filename = fb2Path.getFileName().toString();
        return filename.replace(".fb2", "");
    }

    private Path findConvertedFile(Path dir, String uuid, String extension) throws IOException {
        Path exactMatch = dir.resolve(uuid + extension);
        if (Files.exists(exactMatch)) {
            return exactMatch;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + extension)) {
            for (Path file : stream) {
                if (!Files.isDirectory(file)) {
                    long size = Files.size(file);
                    long lastModified = Files.getLastModifiedTime(file).toMillis();
                    long now = System.currentTimeMillis();
                    if (now - lastModified < TimeUnit.MINUTES.toMillis(5) && size > 0) {
                        return file;
                    }
                }
            }
        }

        return null;
    }

    private String readOutput(Process process) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}