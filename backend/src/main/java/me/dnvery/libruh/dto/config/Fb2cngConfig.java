package me.dnvery.libruh.dto.config;

import lombok.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fb2cngConfig {
    private int version = 1;
    private DocumentConfig document = new DocumentConfig();
    private LoggingConfig logging = new LoggingConfig();
    private ReportingConfig reporting = new ReportingConfig();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DocumentConfig {
        private boolean fixZip = false;
        private boolean openFromCover = false;
        private String tocType = "normal";
        private String outputNameTemplate = "";
        private boolean fileNameTransliterate = false;
        private boolean insertSoftHyphen = false;
        private ImagesConfig images = new ImagesConfig();
        private FootnotesConfig footnotes = new FootnotesConfig();
        private AnnotationConfig annotation = new AnnotationConfig();
        private TocPageConfig tocPage = new TocPageConfig();
        private MetainformationConfig metainformation = new MetainformationConfig();
        private VignettesConfig vignettes = new VignettesConfig();
        private DropcapsConfig dropcaps = new DropcapsConfig();
        private PageMapConfig pageMap = new PageMapConfig();
        private TextTransformationsConfig textTransformations = new TextTransformationsConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ImagesConfig {
        private boolean useBroken = false;
        private boolean removeTransparency = false;
        private int scaleFactor = 1;
        private boolean optimize = true;
        private int jpegQualityLevel = 95;
        private ScreenConfig screen = new ScreenConfig();
        private CoverConfig cover = new CoverConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScreenConfig {
        private int width = 1264;
        private int height = 1680;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CoverConfig {
        private boolean generate = false;
        private String resize = "stretch";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FootnotesConfig {
        private String mode = "default";
        private List<String> bodies = new ArrayList<>(List.of("notes", "comments"));
        private String backlinks = "[<]";
        private String moreParagraphs = "(~)\u00A0";
        private String labelTemplate = "";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AnnotationConfig {
        private boolean enable = false;
        private String title = "Annotation";
        private boolean inToc = true;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TocPageConfig {
        private String placement = "none";
        private String authorsTemplate = "";
        private boolean includeChaptersWithoutTitle = false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MetainformationConfig {
        private String titleTemplate = "";
        private String creatorNameTemplate = "";
        private boolean transliterate = false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VignettesConfig {
        private Map<String, Object> book = new LinkedHashMap<>();
        private Map<String, Object> chapter = new LinkedHashMap<>();
        private Map<String, Object> section = new LinkedHashMap<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DropcapsConfig {
        private boolean enable = false;
        private String ignoreSymbols = "";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PageMapConfig {
        private boolean enable = true;
        private int size = 2300;
        private boolean adobeDe = false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TextTransformationsConfig {
        private SpeechConfig speech = new SpeechConfig();
        private DashesConfig dashes = new DashesConfig();
        private DialogueConfig dialogue = new DialogueConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SpeechConfig {
        private boolean enable = false;
        private String from = "";
        private String to = "";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DashesConfig {
        private boolean enable = false;
        private String from = "";
        private String to = "";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DialogueConfig {
        private boolean enable = false;
        private String from = "";
        private String to = "";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoggingConfig {
        private FileLoggingConfig file = new FileLoggingConfig();
        private ConsoleLoggingConfig console = new ConsoleLoggingConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FileLoggingConfig {
        private String level = "debug";
        private String destination = "fb2cng.log";
        private String mode = "overwrite";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ConsoleLoggingConfig {
        private String level = "normal";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReportingConfig {
        private String destination = "fb2cng-report.zip";
    }
}