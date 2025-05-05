//package com.alinesno.infra.base.search.crew;
//
//import com.alinesno.infra.base.search.api.CrawlerDto;
//import com.alinesno.infra.common.web.log.utils.SpringUtils;
//import com.alinesno.infra.smart.im.service.ISSEService;
//import com.spire.doc.Document;
//import com.xxl.crawler.XxlCrawler;
//import com.xxl.crawler.pageloader.param.Response;
//import com.xxl.crawler.pageparser.PageParser;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.regex.Pattern;
//
///**
// * WebCrawler 类是一个用于爬取网站内容并解析不同类型文件的工具类。
// * 它支持爬取 HTML 页面、解析 PDF、Word 和 Excel 文件，并将解析结果存储在 WebContentInfo 对象中。
// */
//@Slf4j
//public class DatasetWebCrawlerBak {
//
//    private static final int CONNECTION_TIMEOUT = 60*1000; // 连接超时时间，单位毫秒
//    private static final int READ_TIMEOUT = 60*1000; // 读取超时时间，单位毫秒
//    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s]");
//
//    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
//
//    /**
//     * 解析指定网站的内容，根据 CSS 选择器提取 HTML 页面内容，
//     * 并解析 PDF、Word 和 Excel 文件。
//     *
//     * @param baseUrl     要解析的网站的基础 URL
//     * @param cssSelector 用于提取 HTML 页面内容的 CSS 选择器
//     * @param dto
//     * @return 存储解析结果的 WebContentInfo 列表
//     */
//    @SneakyThrows
//    public List<WebContentInfo> parseWebsite(String baseUrl, String cssSelector, CrawlerDto dto) {
//
//        ISSEService sseService = SpringUtils.getBean(ISSEService.class);
//
//        // 如果baseUrl最后带有/号的则删除掉
//        if (baseUrl.endsWith("/")) {
//            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
//        }
//
//        List<WebContentInfo> result = new ArrayList<>();
//        Set<String> processedUrls = new HashSet<>();
//
//        String content = String.format("开始同步网站 %s 内容...", baseUrl);
//        sendMessage(sseService, dto.getChannelStreamId(), content);
//
//        XxlCrawler crawler = new XxlCrawler.Builder()
//                .setUrls(dto.getUrls())
//                .setAllowSpread(dto.isAllowSpread())
//                .setWhiteUrlRegexs(baseUrl + "/.*")
//                .setThreadCount(3)
//                .setUserAgent(USER_AGENT)
//                .setFailRetryCount(3)
//                .setPauseMillis(100)
//                .setPageParser(new PageParser<String>() {
//
//                    @SneakyThrows
//                    @Override
//                    public void afterParse(Response<String> response) {
//
//                        String url = response.getRequest().getUrl();
//                        String processedUrl = url.split("#")[0];
//
//                        if (!processedUrls.contains(processedUrl)) {
//                            processedUrls.add(processedUrl);
//                            org.jsoup.nodes.Document doc = response.getHtml();
//                            String title = "";
//                            StringBuilder content = new StringBuilder();
//                            String type = "";
//
//                            sendMessage(sseService, dto.getChannelStreamId(), String.format("正在同步 %s 内容...", decodeUrl(processedUrl)));
//
//                            if (processedUrl.endsWith(".pdf")) {
//                                content = new StringBuilder(parsePDF(processedUrl));
//                                type = "pdf";
//                                title = getFileNameFromUrl(processedUrl);
//                            } else if (processedUrl.endsWith(".docx") || processedUrl.endsWith(".doc")) {
//                                content = new StringBuilder(parseWord(processedUrl));
//                                type = "docx";
//                                title = getFileNameFromUrl(processedUrl);
//                            } else if (processedUrl.endsWith(".xlsx") || processedUrl.endsWith(".xls")) {
//                                content = new StringBuilder(parseExcel(processedUrl));
//                                type = "xlsx";
//                                title = getFileNameFromUrl(processedUrl);
//                            } else {
//                                if (StringUtils.isNotBlank(cssSelector)) {
//                                    Elements elements = doc.select(cssSelector);
//                                    for (Element element : elements) {
//                                        content.append(element.text());
//                                    }
//                                } else {
//                                    content = new StringBuilder(doc.text());
//                                }
//                                type = "html";
//                                title = doc.title();
//                            }
//
//                            // sse输出标题和内容
//                            sendMessage(sseService, dto.getChannelStreamId(), String.format("正在同步 %s 内容", title));
//                            sendMessage(sseService, dto.getChannelStreamId(), String.format("正在同步的内容是:%s", content));
//
//                            sendMessage(sseService, dto.getChannelStreamId(), String.format("同步 %s 内容完成...", decodeUrl(processedUrl)));
//
//                            String cleanContent = content.toString();
//                            if (!cleanContent.trim().isEmpty()) {
//                                WebContentInfo info = new WebContentInfo();
//                                info.setUrl(processedUrl);
//                                info.setTitle(title);
//                                info.setContent(cleanContent);
//                                info.setType(type);
//                                result.add(info);
//                                log.info("Parsed: {}", processedUrl);
//                            }
//                        }
//                    }
//                })
//                .build();
//
//        crawler.start(true);
//        sendMessage(sseService, dto.getChannelStreamId(), String.format("同步 %s 完成...", baseUrl));
//
//        if(sseService != null){
//            sseService.sendDone(dto.getChannelStreamId());
//        }
//
//        crawler.getRunUrlPool().getUrlNum();
//
//        return result;
//    }
//
//    @SneakyThrows
//    private static void sendMessage(ISSEService sseService, String channelStreamId, String content) {
//        if(sseService == null){
//            return ;
//        }
//        sseService.sendNotDone(channelStreamId, content);
//    }
//
//    /**
//     * 从 URL 中提取文件名
//     * @param url 文件的 URL
//     * @return 文件名
//     */
//    private String getFileNameFromUrl(String url) {
//        int lastIndex = url.lastIndexOf("/");
//        if (lastIndex != -1) {
//            return url.substring(lastIndex + 1);
//        }
//        return url;
//    }
//
//    /**
//     * 去除字符串中的特殊字符
//     * @param input 输入字符串
//     * @return 去除特殊字符后的字符串
//     */
//    private String removeSpecialCharacters(String input) {
//        return SPECIAL_CHAR_PATTERN.matcher(input).replaceAll("");
//    }
//
//    /**
//     * 对 URL 编码的字符串进行解码
//     * @param encodedUrl 编码后的 URL 字符串
//     * @return 解码后的字符串，如果解码失败则返回 null
//     */
//    public String decodeUrl(String encodedUrl) {
//        return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
//    }
//
//    /**
//     * 解析 PDF 文件内容。
//     *
//     * @param url PDF 文件的 URL
//     * @return PDF 文件的文本内容
//     * @throws IOException 读取文件时可能出现的异常
//     */
//    private String parsePDF(String url) throws IOException {
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setConnectTimeout(CONNECTION_TIMEOUT);
//        connection.setReadTimeout(READ_TIMEOUT);
//        try (InputStream inputStream = connection.getInputStream();
//             PDDocument document = PDDocument.load(inputStream)) {
//            PDFTextStripper pdfStripper = new PDFTextStripper();
//            return pdfStripper.getText(document);
//        }
//    }
//
//    /**
//     * 解析 Word 文件内容。
//     *
//     * @param url Word 文件的 URL
//     * @return Word 文件的文本内容
//     * @throws IOException 读取文件时可能出现的异常
//     */
//    private String parseWord(String url) throws IOException {
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setConnectTimeout(CONNECTION_TIMEOUT);
//        connection.setReadTimeout(READ_TIMEOUT);
//        Path tempFile = Files.createTempFile("temp", ".docx");
//        try (InputStream inputStream = connection.getInputStream()) {
//            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//            Document doc = new Document();
//            doc.loadFromFile(tempFile.toString());
//
//            return doc.getText();
//        } finally {
//            Files.deleteIfExists(tempFile);
//        }
//    }
//
//    /**
//     * 解析 Excel 文件内容。
//     *
//     * @param url Excel 文件的 URL
//     * @return Excel 文件的文本内容
//     * @throws IOException 读取文件时可能出现的异常
//     */
//    private String parseExcel(String url) throws IOException {
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setConnectTimeout(CONNECTION_TIMEOUT);
//        connection.setReadTimeout(READ_TIMEOUT);
//        StringBuilder content = new StringBuilder();
//        try (InputStream inputStream = connection.getInputStream()) {
//            Workbook workbook;
//            if (url.endsWith(".xlsx")) {
//                workbook = new XSSFWorkbook(inputStream);
//            } else if (url.endsWith(".xls")) {
//                workbook = new HSSFWorkbook(inputStream);
//            } else {
//                throw new IOException("Unsupported Excel file format");
//            }
//
//            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//                Sheet sheet = workbook.getSheetAt(i);
//                for (Row row : sheet) {
//                    for (Cell cell : row) {
//                        content.append(cell.toString()).append("\t");
//                    }
//                    content.append("\n");
//                }
//            }
//        }
//        return content.toString();
//    }
//
//}