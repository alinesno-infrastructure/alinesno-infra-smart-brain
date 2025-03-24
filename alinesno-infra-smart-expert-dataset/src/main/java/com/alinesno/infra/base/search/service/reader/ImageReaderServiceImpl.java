package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * ImageReaderServiceImpl 类是图像附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要用于处理图像附件的读取操作。
 */
@Service
public class ImageReaderServiceImpl  extends BaseReaderServiceImpl {

    /**
     * 此方法用于读取指定 ID 的图像附件内容。
     *
     * @param attachmentDto 要读取的图像附件的唯一标识符。
     * @return 返回读取到的图像附件内容，若读取失败或无内容则返回 null。
     */
    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto) {

        File file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());

        // 临时处理成OCR识别服务
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getAbsolutePath() ,
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url("http://alinesno-infra-smart-ocr-boot.beta.base.infra.linesno.com/api/infra/smart/ocr/generalText")
                .method("POST", body)
                .addHeader("Content-Type", "multipart/form-data")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                return response.body().string();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}