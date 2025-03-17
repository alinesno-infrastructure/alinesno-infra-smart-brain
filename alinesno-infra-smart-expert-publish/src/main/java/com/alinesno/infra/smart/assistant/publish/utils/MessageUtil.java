package com.alinesno.infra.smart.assistant.publish.utils;

import com.alinesno.infra.smart.assistant.api.response.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import jakarta.servlet.http.HttpServletRequest;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息工具类，提供了处理微信消息相关的功能，包括消息类型常量定义、XML 解析和对象与 XML 之间的转换。
 */
@Slf4j
public class MessageUtil {
    // 返回消息类型常量
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";

    // 请求消息类型常量
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
    public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
    public static final String REQ_MESSAGE_TYPE_SHORTVIDEO = "shortvideo";
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
    public static final String REQ_MESSAGE_TYPE_LINK = "link";
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";

    // 事件类型常量
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    public static final String EVENT_TYPE_CLICK = "CLICK";
    public static final String EVENT_TYPE_SCAN = "SCAN";
    public static final String EVENT_TYPE_LOCATION = "LOCATION";
    public static final String EVENT_TYPE_VIEW = "VIEW";

    /**
     * 解析微信发来的请求（XML 格式），将 XML 数据转换为键值对的 Map。
     *
     * @param request HttpServletRequest 对象，包含微信发来的请求数据
     * @return 包含解析后数据的 Map，键为 XML 元素名，值为元素文本内容
     * @throws Exception 解析过程中可能出现的异常
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<>();
        try (InputStream inputStream = request.getInputStream()) {
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
        } catch (Exception e) {
            log.error("解析微信请求 XML 时出现异常", e);
            throw e;
        }
        return map;
    }

    /**
     * 自定义 XStream 驱动，用于在转换 XML 时为所有节点添加 CDATA 标记。
     */
    private static XStream xstream = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                boolean cdata = true;

                @Override
                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }

                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    /**
     * 将文本消息对象转换为 XML 字符串。
     *
     * @param textMessageRes 文本消息响应对象
     * @return 转换后的 XML 字符串
     */
    public static String textMessageToXml(TextMessageRes textMessageRes) {
        xstream.alias("xml", textMessageRes.getClass());
        return xstream.toXML(textMessageRes);
    }

    /**
     * 将图文消息对象转换为 XML 字符串。
     * 此方法目前被注释掉，可根据需要取消注释使用。
     *
     * @param articlesMessageRes 图文消息响应对象
     * @return 转换后的 XML 字符串
     */
//    public static String newsMessageToXml(ArticlesMessageRes articlesMessageRes) {
//        xstream.alias("xml", articlesMessageRes.getClass());
//        xstream.alias("item", new Article().getClass());
//        return xstream.toXML(articlesMessageRes);
//    }

    /**
     * 将图片消息对象转换为 XML 字符串。
     *
     * @param imageMessageRes 图片消息响应对象
     * @return 转换后的 XML 字符串
     */
    public static String imageMessageToXml(ImageMessageRes imageMessageRes) {
        xstream.alias("xml", imageMessageRes.getClass());
        return xstream.toXML(imageMessageRes);
    }

    /**
     * 将语音消息对象转换为 XML 字符串。
     *
     * @param voiceMessageRes 语音消息响应对象
     * @return 转换后的 XML 字符串
     */
    public static String voiceMessageToXml(VoiceMessageRes voiceMessageRes) {
        xstream.alias("xml", voiceMessageRes.getClass());
        return xstream.toXML(voiceMessageRes);
    }

    /**
     * 将视频消息对象转换为 XML 字符串。
     *
     * @param videoMessageRes 视频消息响应对象
     * @return 转换后的 XML 字符串
     */
    public static String videoMessageToXml(VideoMessageRes videoMessageRes) {
        xstream.alias("xml", videoMessageRes.getClass());
        return xstream.toXML(videoMessageRes);
    }

    /**
     * 将音乐消息对象转换为 XML 字符串。
     *
     * @param musicMessageRes 音乐消息响应对象
     * @return 转换后的 XML 字符串
     */
    public static String musicMessageToXml(MusicMessageRes musicMessageRes) {
        xstream.alias("xml", musicMessageRes.getClass());
        return xstream.toXML(musicMessageRes);
    }
}