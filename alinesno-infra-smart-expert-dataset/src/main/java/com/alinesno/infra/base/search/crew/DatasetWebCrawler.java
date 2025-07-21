package com.alinesno.infra.base.search.crew;

import com.alinesno.infra.base.search.api.CrawlerDto;
import com.alinesno.infra.smart.im.service.ISSEService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * WebCrawler 类是一个用于爬取网站内容并解析不同类型文件的工具类。
 * 它支持爬取 HTML 页面、解析 PDF、Word 和 Excel 文件，并将解析结果存储在 WebContentInfo 对象中。
 */
@Slf4j
public class DatasetWebCrawler {

    private static final int CONNECTION_TIMEOUT = 60 * 1000; // 连接超时时间，单位毫秒
    private static final int READ_TIMEOUT = 60 * 1000; // 读取超时时间，单位毫秒
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s]");

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";

    /**
     * 解析指定网站的内容，根据 CSS 选择器提取 HTML 页面内容，
     * 并解析 PDF、Word 和 Excel 文件。
     *
     * @param baseUrl     要解析的网站的基础 URL
     * @param cssSelector 用于提取 HTML 页面内容的 CSS 选择器
     * @param dto
     * @return 存储解析结果的 WebContentInfo 列表
     */
    @SneakyThrows
    public List<WebContentInfo> parseWebsite(String baseUrl, String cssSelector, CrawlerDto dto, ISSEService sseService) {

        // 如果baseUrl最后带有/号的则删除掉
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        final String finalBaseUrl = baseUrl;

        List<WebContentInfo> result = new ArrayList<>();
        Set<String> processedUrls = new HashSet<>();
        AtomicInteger pageCount = new AtomicInteger(); // 记录已处理的页面数量

        String content = String.format("开始同步网站 %s 内容...", baseUrl);
        sendMessage(sseService, dto.getChannelStreamId(), content);
        log.info("线程 [{}] 开始同步网站 {} 内容", Thread.currentThread().getName(), finalBaseUrl);

        crawlPage(finalBaseUrl, cssSelector, dto, 0, result, processedUrls, pageCount, sseService);

        log.info("线程 [{}] 所有任务完成，同步 {} 完成，共同步了 {} 个页面...", Thread.currentThread().getName(), finalBaseUrl, pageCount.get());
        if (sseService != null) {
            try {
                sseService.sendDone(dto.getChannelStreamId());
            } catch (IOException e) {
                log.error("Error sending message to SSE: {}", e.getMessage(), e);
            }
        }

        return result;
    }

    private void crawlPage(String url, String cssSelector, CrawlerDto dto, int currentDepth, List<WebContentInfo> result, Set<String> processedUrls, AtomicInteger pageCount, ISSEService sseService) {
        if (pageCount.get() >= dto.getMaxPage() || currentDepth > dto.getMaxDepth()) {
            return;
        }

        if (processedUrls.contains(url)) {
            return;
        }

        processedUrls.add(url);

        try {
            String processedUrl = url.split("#")[0];
            if (!processedUrl.startsWith(dto.getBaseUrl())) {
                return;
            }

            String title = getFileNameFromUrl(processedUrl);
            sendMessage(sseService, dto.getChannelStreamId(), String.format("正在同步 %s 内容 , 当前已同步 %s 个页面", decodeUrl(processedUrl) , pageCount.get()));
            log.info("线程 [{}] 正在同步 {}（名称：{}） 内容，当前已同步 {} 个页面", Thread.currentThread().getName(), decodeUrl(processedUrl), title, pageCount.get());

            org.jsoup.nodes.Document doc = null;
            StringBuilder contentBuilder = new StringBuilder();
            String type = "";

            // TODO 添加图片爬取和识别
            if (dto.isCrawlPdf() && processedUrl.endsWith(".pdf")) {
                contentBuilder = new StringBuilder(parsePDF(processedUrl));
                type = "pdf";
            } else if (dto.isCrawlWord() && (processedUrl.endsWith(".docx") || processedUrl.endsWith(".doc"))) {
                contentBuilder = new StringBuilder(parseWord(processedUrl));
                type = "docx";
            } else if (dto.isCrawlExcel() && (processedUrl.endsWith(".xlsx") || processedUrl.endsWith(".xls"))) {
                contentBuilder = new StringBuilder(parseExcel(processedUrl));
                type = "xlsx";
            } else {
                doc = Jsoup.connect(processedUrl).userAgent(USER_AGENT).timeout(CONNECTION_TIMEOUT).get();
                if (StringUtils.isNotBlank(cssSelector)) {
                    Elements elements = doc.select(cssSelector);
                    for (Element element : elements) {
                        contentBuilder.append(element.text());
                    }
                } else {
                    contentBuilder = new StringBuilder(doc.text());
                }
                type = "html";
                title = doc.title();

                // 递归爬取页面中的链接
                if (currentDepth < dto.getMaxDepth()) {
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String nextUrl = link.absUrl("href");
                        crawlPage(nextUrl, cssSelector, dto, currentDepth + 1, result, processedUrls, pageCount, sseService);
                    }
                }
            }

            // sse输出标题和内容
            sendMessage(sseService, dto.getChannelStreamId(), String.format("正在同步 %s 内容", title));
            sendMessage(sseService, dto.getChannelStreamId(), String.format("正在同步的内容是:%s", contentBuilder));

            sendMessage(sseService, dto.getChannelStreamId(), String.format("同步 %s 内容完成, 当前已同步 %s 个页面 ", decodeUrl(processedUrl) , pageCount.get()));

            String cleanContent = contentBuilder.toString();
            if (!cleanContent.trim().isEmpty()) {
                WebContentInfo info = new WebContentInfo();
                info.setUrl(processedUrl);
                info.setTitle(title);
                info.setContent(cleanContent);
                info.setType(type);
                synchronized (result) {
                    result.add(info);
                }
                log.info("线程 [{}] 解析完成: {}（名称：{}）, 当前已同步 {} 个页面", Thread.currentThread().getName(), processedUrl, title, pageCount.get());
                pageCount.getAndIncrement(); // 成功处理一个页面，增加计数
            }
        } catch (IOException e) {
            log.error("线程 [{}] Error processing URL: {}", Thread.currentThread().getName(), url, e);
        }
    }

    @SneakyThrows
    private static void sendMessage(ISSEService sseService, String channelStreamId, String content) {
        if (sseService == null) {
            return;
        }
        sseService.sendNotDone(channelStreamId, content);
    }

    /**
     * 从 URL 中提取文件名
     *
     * @param url 文件的 URL
     * @return 文件名
     */
    private String getFileNameFromUrl(String url) {
        int lastIndex = url.lastIndexOf("/");
        if (lastIndex != -1) {
            return url.substring(lastIndex + 1);
        }
        return url;
    }

    /**
     * 去除字符串中的特殊字符
     *
     * @param input 输入字符串
     * @return 去除特殊字符后的字符串
     */
    private String removeSpecialCharacters(String input) {
        return SPECIAL_CHAR_PATTERN.matcher(input).replaceAll("");
    }

    /**
     * 对 URL 编码的字符串进行解码
     *
     * @param encodedUrl 编码后的 URL 字符串
     * @return 解码后的字符串，如果解码失败则返回 null
     */
    public String decodeUrl(String encodedUrl) {
        return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
    }

    /**
     * 解析 PDF 文件内容。
     *
     * @param url PDF 文件的 URL
     * @return PDF 文件的文本内容
     * @throws IOException 读取文件时可能出现的异常
     */
    private String parsePDF(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        try (InputStream inputStream = connection.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    /**
     * 解析 Word 文件内容。
     *
     * @param url Word 文件的 URL
     * @return Word 文件的文本内容
     * @throws IOException 读取文件时可能出现的异常
     */
    private String parseWord(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        Path tempFile = Files.createTempFile("temp", getFileExtension(url));

        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            String content;
            if (url.toLowerCase().endsWith(".docx")) {
                // 处理.docx文件
                try (XWPFDocument document = new XWPFDocument(Files.newInputStream(tempFile));
                     XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    content = extractor.getText();
                }
            } else if (url.toLowerCase().endsWith(".doc")) {
                // 处理.doc文件
                try (HWPFDocument document = new HWPFDocument(Files.newInputStream(tempFile));
                     WordExtractor extractor = new WordExtractor(document)) {
                    content = extractor.getText();
                }
            } else {
                throw new IllegalArgumentException("不支持的文件格式: " + url);
            }

            return content;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * 根据URL获取文件扩展名
     */
    private String getFileExtension(String url) {
        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return url.substring(lastDotIndex);
        }
        return ".tmp"; // 默认扩展名
    }

    /**
     * 解析 Excel 文件内容。
     *
     * @param url Excel 文件的 URL
     * @return Excel 文件的文本内容
     * @throws IOException 读取文件时可能出现的异常
     */
    private String parseExcel(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = connection.getInputStream()) {
            Workbook workbook;
            if (url.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (url.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                throw new IOException("Unsupported Excel file format");
            }

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        content.append(cell.toString()).append("\t");
                    }
                    content.append("\n");
                }
            }
        }
        return content.toString();
    }
}


