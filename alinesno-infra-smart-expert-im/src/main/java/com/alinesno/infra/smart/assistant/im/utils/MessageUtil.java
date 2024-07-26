package com.alinesno.infra.smart.assistant.im.utils;

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

import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    /**
     * 返回消息类型：文本
     */
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
 
    /**
     * 返回消息类型：音乐
     */
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
 
    /**
     * 返回消息类型：图文
     */
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";
 
 
    /**
     * 请求消息类型：文本
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
 
    /**
     * 请求消息类型：图片
     */
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
 
    /**
     * 请求消息类型：语音
     */
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
 
    /**
     * 请求消息类型：视频
     */
    public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
 
    /**
     * 请求消息类型：小视频
     */
    public static final String REQ_MESSAGE_TYPE_SHORTVIDEO = "shortvideo";
 
    /**
     * 请求消息类型：地理位置
     */
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
 
    /**
     * 请求消息类型：链接
     */
    public static final String REQ_MESSAGE_TYPE_LINK = "link";
 
 
 
    /**
     * 请求消息类型：推送
     */
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";
 
    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
 
    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
 
    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_CLICK = "CLICK";
 
    /**
     * 事件类型：VIEW(扫描二维码事件)
     */
    public static final String EVENT_TYPE_SCAN = "SCAN";
 
    /**
     * 事件类型：LOCATION(位置上报事件)
     */
    public static final String EVENT_TYPE_LOCATION = "LOCATION";
 
    /**
     * 事件类型：VIEW(自定义菜单View事件)
     */
    public static final String EVENT_TYPE_VIEW = "VIEW";
 
 
    /**
     * 解析微信发来的请求（XML）
     **/
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
 
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
 
        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());
 
        // 释放资源
        inputStream.close();
        inputStream = null;
 
        return map;
    }
 
    /**
     * 对象到xml的处理
     **/
    @SuppressWarnings("unused")
    private static final XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;
                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }
 
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
     * 文本消息对象转换成xml
     **/
    public static String textMessageToXml(TextMessageRes textMessageRes) {
        xstream.alias("xml", textMessageRes.getClass());
        return xstream.toXML(textMessageRes);
    }
 
    /**
     * 图文消息对象转换成xml
     **/
    public static String newsMessageToXml(ArticlesMessageRes articlesMessageRes) {
        xstream.alias("xml", articlesMessageRes.getClass());
//        xstream.alias("item", new Article().getClass());
        return xstream.toXML(articlesMessageRes);
    }
 
    /**
     * 图片消息对象转换成xml
     **/
    public static String imageMessageToXml(ImageMessageRes imageMessageRes) {
        xstream.alias("xml", imageMessageRes.getClass());
        return xstream.toXML(imageMessageRes);
    }
 
    /**
     * 语音消息对象转换成xml
     **/
    public static String voiceMessageToXml(VoiceMessageRes voiceMessageRes) {
        xstream.alias("xml", voiceMessageRes.getClass());
        return xstream.toXML(voiceMessageRes);
    }
 
    /**
     * 视频消息对象转换成xml
     **/
    public static String videoMessageToXml(VideoMessageRes videoMessageRes) {
        xstream.alias("xml", videoMessageRes.getClass());
        return xstream.toXML(videoMessageRes);
    }
 
    /**
     * 音乐消息对象转换成xml
     **/
    public static String musicMessageToXml(MusicMessageRes musicMessageRes) {
        xstream.alias("xml", musicMessageRes.getClass());
        return xstream.toXML(musicMessageRes);
    }
}
 