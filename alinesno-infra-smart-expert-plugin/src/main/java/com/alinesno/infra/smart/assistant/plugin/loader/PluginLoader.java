package com.alinesno.infra.smart.assistant.plugin.loader;

import com.alinesno.infra.smart.assistant.role.utils.YAMLMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载插件
 */
@Slf4j
public class PluginLoader {

    /**
     * 自动从插件中心下载jar插件到本地目录，使用ok-http下载，如果本地已经存在，则直接覆盖，更新到最新的插件
     * 插件中心根目录包含有一个`plugins.txt`的文件，以properties形式列出插件，格式`插件名称=插件位置 `,如下示例:
     *
     * database_document_write_agent=database_document_write_agent-1.0.0.jar
     * product_document_write_agent=product_document_write_agent-1.0.0.jar
     *
     * 在jar包里面的`MANIFEST.MF`包含jar包的基础信息，主要内容字段如下:
     *
     * pluginName: jar包插件的名称
     * pluginDesc: jar包插件的作用描述信息
     * pluginAuthor: 插件的作者信息，比如hangman<hangman@gmail.com/>
     *
     * @param pluginCentralUrl 插件中心的URL，例如 <a href="http://plugin.central.com/plugins.yaml">...</a>
     * @param localPluginPath  从插件中心下载到本地目录
     * @return 插件信息列表
     */
    @SneakyThrows
    public static List<PluginInfo> loadPlugin(String pluginCentralUrl, String localPluginPath) {

        File file = new File(localPluginPath) ;
        if(!file.exists()){
            FileUtils.forceMkdir(file);
        }

        List<PluginInfo> pluginInfoList = new ArrayList<>();
        try {
            // 读取插件中心的配置文件
            URL url = new URL(pluginCentralUrl + "plugins.yaml");
            String pluginYaml = IOUtils.toString(url, StandardCharsets.UTF_8);
            log.debug("pluginYaml = {}" , pluginYaml);

            Agents agent = YAMLMapper.fromYAML(pluginYaml , Agents.class) ;

            // 循环下载插件
            for (PluginInfo plugin : agent.agents) {

                log.debug("plugin = {}" , plugin);

                String pluginJar = plugin.getJarName() ;

                String pluginUrl = pluginCentralUrl + pluginJar;
                String localPath = localPluginPath + "/" + pluginJar ;

                if(!new File(localPath).exists()){
                    log.debug("下载插件:{}" , pluginUrl);
                    downloadPlugin(pluginUrl, localPath);
                    log.debug("下载插件成功:{}" , localPath);

                }else{
                    log.debug("本地插件已经存在:{}" , localPath);
                }

                pluginInfoList.add(plugin) ;
            }
        } catch (IOException e) {
            log.error("下载异常: {}", e.getMessage());
        }

        return pluginInfoList;
    }


    /**
     * 从指定URL下载插件并保存到本地目录
     *
     * @param pluginUrl       插件的URL
     * @param localPluginPath 本地保存路径
     * @throws IOException 可能抛出的IO异常
     */
    public static void downloadPlugin(String pluginUrl, String localPluginPath) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pluginUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("下载插件失败: " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                // 将插件保存到本地
                try (InputStream inputStream = responseBody.byteStream();
                     FileOutputStream outputStream = new FileOutputStream(localPluginPath)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
    }

    /**
     * 专家列表
     */
    @Data
    public static class Agents {
        private List<PluginInfo> agents ;
    }

    /**
     * 插件信息类
     */
    @Data
    public static class PluginInfo {
        private String name;
        private String desc;
        private String author;
        private String jarName;
        private String localPath;
    }

}
