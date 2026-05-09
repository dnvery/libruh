package me.dnvery.libruh.dto.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Fb2cngConfig {
    private int version = 1;
    private DocumentConfig document = new DocumentConfig();
    private LoggingConfig logging = new LoggingConfig();
    private ReportingConfig reporting = new ReportingConfig();

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public DocumentConfig getDocument() { return document; }
    public void setDocument(DocumentConfig document) { this.document = document; }
    public LoggingConfig getLogging() { return logging; }
    public void setLogging(LoggingConfig logging) { this.logging = logging; }
    public ReportingConfig getReporting() { return reporting; }
    public void setReporting(ReportingConfig reporting) { this.reporting = reporting; }

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

        public boolean isFixZip() { return fixZip; }
        public void setFixZip(boolean fixZip) { this.fixZip = fixZip; }
        public boolean isOpenFromCover() { return openFromCover; }
        public void setOpenFromCover(boolean openFromCover) { this.openFromCover = openFromCover; }
        public String getTocType() { return tocType; }
        public void setTocType(String tocType) { this.tocType = tocType; }
        public String getOutputNameTemplate() { return outputNameTemplate; }
        public void setOutputNameTemplate(String outputNameTemplate) { this.outputNameTemplate = outputNameTemplate; }
        public boolean isFileNameTransliterate() { return fileNameTransliterate; }
        public void setFileNameTransliterate(boolean fileNameTransliterate) { this.fileNameTransliterate = fileNameTransliterate; }
        public boolean isInsertSoftHyphen() { return insertSoftHyphen; }
        public void setInsertSoftHyphen(boolean insertSoftHyphen) { this.insertSoftHyphen = insertSoftHyphen; }
        public ImagesConfig getImages() { return images; }
        public void setImages(ImagesConfig images) { this.images = images; }
        public FootnotesConfig getFootnotes() { return footnotes; }
        public void setFootnotes(FootnotesConfig footnotes) { this.footnotes = footnotes; }
        public AnnotationConfig getAnnotation() { return annotation; }
        public void setAnnotation(AnnotationConfig annotation) { this.annotation = annotation; }
        public TocPageConfig getTocPage() { return tocPage; }
        public void setTocPage(TocPageConfig tocPage) { this.tocPage = tocPage; }
        public MetainformationConfig getMetainformation() { return metainformation; }
        public void setMetainformation(MetainformationConfig metainformation) { this.metainformation = metainformation; }
        public VignettesConfig getVignettes() { return vignettes; }
        public void setVignettes(VignettesConfig vignettes) { this.vignettes = vignettes; }
        public DropcapsConfig getDropcaps() { return dropcaps; }
        public void setDropcaps(DropcapsConfig dropcaps) { this.dropcaps = dropcaps; }
        public PageMapConfig getPageMap() { return pageMap; }
        public void setPageMap(PageMapConfig pageMap) { this.pageMap = pageMap; }
        public TextTransformationsConfig getTextTransformations() { return textTransformations; }
        public void setTextTransformations(TextTransformationsConfig textTransformations) { this.textTransformations = textTransformations; }
    }

    public static class ImagesConfig {
        private boolean useBroken = false;
        private boolean removeTransparency = false;
        private int scaleFactor = 1;
        private boolean optimize = true;
        private int jpegQualityLevel = 95;
        private ScreenConfig screen = new ScreenConfig();
        private CoverConfig cover = new CoverConfig();

        public boolean isUseBroken() { return useBroken; }
        public void setUseBroken(boolean useBroken) { this.useBroken = useBroken; }
        public boolean isRemoveTransparency() { return removeTransparency; }
        public void setRemoveTransparency(boolean removeTransparency) { this.removeTransparency = removeTransparency; }
        public int getScaleFactor() { return scaleFactor; }
        public void setScaleFactor(int scaleFactor) { this.scaleFactor = scaleFactor; }
        public boolean isOptimize() { return optimize; }
        public void setOptimize(boolean optimize) { this.optimize = optimize; }
        public int getJpegQualityLevel() { return jpegQualityLevel; }
        public void setJpegQualityLevel(int jpegQualityLevel) { this.jpegQualityLevel = jpegQualityLevel; }
        public ScreenConfig getScreen() { return screen; }
        public void setScreen(ScreenConfig screen) { this.screen = screen; }
        public CoverConfig getCover() { return cover; }
        public void setCover(CoverConfig cover) { this.cover = cover; }
    }

    public static class ScreenConfig {
        private int width = 1264;
        private int height = 1680;

        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
    }

    public static class CoverConfig {
        private boolean generate = false;
        private String resize = "stretch";

        public boolean isGenerate() { return generate; }
        public void setGenerate(boolean generate) { this.generate = generate; }
        public String getResize() { return resize; }
        public void setResize(String resize) { this.resize = resize; }
    }

    public static class FootnotesConfig {
        private String mode = "default";
        private List<String> bodies = new ArrayList<>(List.of("notes", "comments"));
        private String backlinks = "[<]";
        private String moreParagraphs = "(~)\u00A0";
        private String labelTemplate = "";

        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public List<String> getBodies() { return bodies; }
        public void setBodies(List<String> bodies) { this.bodies = bodies; }
        public String getBacklinks() { return backlinks; }
        public void setBacklinks(String backlinks) { this.backlinks = backlinks; }
        public String getMoreParagraphs() { return moreParagraphs; }
        public void setMoreParagraphs(String moreParagraphs) { this.moreParagraphs = moreParagraphs; }
        public String getLabelTemplate() { return labelTemplate; }
        public void setLabelTemplate(String labelTemplate) { this.labelTemplate = labelTemplate; }
    }

    public static class AnnotationConfig {
        private boolean enable = false;
        private String title = "Annotation";
        private boolean inToc = true;

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public boolean isInToc() { return inToc; }
        public void setInToc(boolean inToc) { this.inToc = inToc; }
    }

    public static class TocPageConfig {
        private String placement = "none";
        private String authorsTemplate = "";
        private boolean includeChaptersWithoutTitle = false;

        public String getPlacement() { return placement; }
        public void setPlacement(String placement) { this.placement = placement; }
        public String getAuthorsTemplate() { return authorsTemplate; }
        public void setAuthorsTemplate(String authorsTemplate) { this.authorsTemplate = authorsTemplate; }
        public boolean isIncludeChaptersWithoutTitle() { return includeChaptersWithoutTitle; }
        public void setIncludeChaptersWithoutTitle(boolean includeChaptersWithoutTitle) { this.includeChaptersWithoutTitle = includeChaptersWithoutTitle; }
    }

    public static class MetainformationConfig {
        private String titleTemplate = "";
        private String creatorNameTemplate = "";
        private boolean transliterate = false;

        public String getTitleTemplate() { return titleTemplate; }
        public void setTitleTemplate(String titleTemplate) { this.titleTemplate = titleTemplate; }
        public String getCreatorNameTemplate() { return creatorNameTemplate; }
        public void setCreatorNameTemplate(String creatorNameTemplate) { this.creatorNameTemplate = creatorNameTemplate; }
        public boolean isTransliterate() { return transliterate; }
        public void setTransliterate(boolean transliterate) { this.transliterate = transliterate; }
    }

    public static class VignettesConfig {
        private Map<String, Object> book = new LinkedHashMap<>();
        private Map<String, Object> chapter = new LinkedHashMap<>();
        private Map<String, Object> section = new LinkedHashMap<>();

        public Map<String, Object> getBook() { return book; }
        public void setBook(Map<String, Object> book) { this.book = book; }
        public Map<String, Object> getChapter() { return chapter; }
        public void setChapter(Map<String, Object> chapter) { this.chapter = chapter; }
        public Map<String, Object> getSection() { return section; }
        public void setSection(Map<String, Object> section) { this.section = section; }
    }

    public static class DropcapsConfig {
        private boolean enable = false;
        private String ignoreSymbols = "";

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }
        public String getIgnoreSymbols() { return ignoreSymbols; }
        public void setIgnoreSymbols(String ignoreSymbols) { this.ignoreSymbols = ignoreSymbols; }
    }

    public static class PageMapConfig {
        private boolean enable = true;
        private int size = 2300;
        private boolean adobeDe = false;

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public boolean isAdobeDe() { return adobeDe; }
        public void setAdobeDe(boolean adobeDe) { this.adobeDe = adobeDe; }
    }

    public static class TextTransformationsConfig {
        private SpeechConfig speech = new SpeechConfig();
        private DashesConfig dashes = new DashesConfig();
        private DialogueConfig dialogue = new DialogueConfig();

        public SpeechConfig getSpeech() { return speech; }
        public void setSpeech(SpeechConfig speech) { this.speech = speech; }
        public DashesConfig getDashes() { return dashes; }
        public void setDashes(DashesConfig dashes) { this.dashes = dashes; }
        public DialogueConfig getDialogue() { return dialogue; }
        public void setDialogue(DialogueConfig dialogue) { this.dialogue = dialogue; }
    }

    public static class SpeechConfig {
        private boolean enable = false;
        private String from = "";
        private String to = "";

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
    }

    public static class DashesConfig {
        private boolean enable = false;
        private String from = "";
        private String to = "";

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
    }

    public static class DialogueConfig {
        private boolean enable = false;
        private String from = "";
        private String to = "";

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
    }

    public static class LoggingConfig {
        private FileLoggingConfig file = new FileLoggingConfig();
        private ConsoleLoggingConfig console = new ConsoleLoggingConfig();

        public FileLoggingConfig getFile() { return file; }
        public void setFile(FileLoggingConfig file) { this.file = file; }
        public ConsoleLoggingConfig getConsole() { return console; }
        public void setConsole(ConsoleLoggingConfig console) { this.console = console; }
    }

    public static class FileLoggingConfig {
        private String level = "debug";
        private String destination = "fb2cng.log";
        private String mode = "overwrite";

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
    }

    public static class ConsoleLoggingConfig {
        private String level = "normal";

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
    }

    public static class ReportingConfig {
        private String destination = "fb2cng-report.zip";

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
    }
}