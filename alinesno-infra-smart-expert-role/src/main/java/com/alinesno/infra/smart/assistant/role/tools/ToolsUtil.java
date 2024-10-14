package com.alinesno.infra.smart.assistant.role.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * 工具类，提供文件操作的相关方法
 */
public class ToolsUtil {

    /**
     * 合并音频文件
     * 将多个音频文件合并成一个单一的音频文件
     *
     * @param outFile 合并后的输出文件路径
     * @param inFiles 需要合并的音频文件路径列表
     * @return 如果合并成功，返回true
     * @throws Exception 如果合并过程中发生错误，抛出异常
     */
    public boolean combine(String outFile, List<String> inFiles) throws Exception {

        // 将文件路径转换为File对象数组
        File[] files = new File[inFiles.size()];
        for (int i = 0; i < files.length; i++)
        {
            files[i] = new File(inFiles.get(i));
        }

        // 使用FileInputStream读取输入文件，FileOutputStream写入输出文件
        FileInputStream fis = null;
        FileOutputStream fos = new FileOutputStream(outFile, true); // 追加模式写入

        // 遍历所有文件，逐个读取并写入到输出文件中
        for (File file : files) {
            fis = new FileInputStream(file);
            int len = 0;
            // 使用1MB的缓冲区读取文件内容
            for (byte[] buf = new byte[1024 * 1024]; (len = fis.read(buf)) != -1; ) {
                fos.write(buf, 0, len);
            }
        }

        // 确保输入文件流不为空，然后关闭所有流
        assert fis != null;
        fis.close();
        fos.close();

        return true;
    }
}
