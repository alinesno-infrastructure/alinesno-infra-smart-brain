package com.alinesno.infra.smart.assistant.adapter.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.alinesno.infra.smart.assistant.entity.StorageFileEntity;
import com.alinesno.infra.smart.assistant.service.IStorageFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用来将文件上传记录保存到数据库，这里使用了 MyBatis-Plus 和 Hutool 工具类
 */
@Service
public class FileDetailService implements FileRecorder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IStorageFileService fileService ;

    /**
     * 保存文件信息到数据库
     */
    @SneakyThrows
    @Override
    public boolean save(FileInfo info) {
        StorageFileEntity detail = BeanUtil.copyProperties(info, StorageFileEntity.class,"metadata","userMetadata","thMetadata","thUserMetadata","attr");

        //这是手动获 元数据 并转成 json 字符串，方便存储在数据库中
        detail.setMetadata(valueToJson(info.getMetadata()));
        detail.setUserMetadata(valueToJson(info.getUserMetadata()));
        detail.setThMetadata(valueToJson(info.getThMetadata()));
        detail.setThUserMetadata(valueToJson(info.getThUserMetadata()));

        //这是手动获 取附加属性字典 并转成 json 字符串，方便存储在数据库中
        detail.setAttr(valueToJson(info.getAttr()));

        boolean b = fileService.save(detail);
        if (b) {
            info.setId(detail.getId()+"");
        }
        return b;
    }

    /**
     * 根据 url 查询文件信息
     */
    @SneakyThrows
    @Override
    public FileInfo getByUrl(String url) {
        StorageFileEntity detail = fileService.getOne(new LambdaQueryWrapper<StorageFileEntity>().eq(StorageFileEntity::getUrl,url));
        FileInfo info = BeanUtil.copyProperties(detail,FileInfo.class,"metadata","userMetadata","thMetadata","thUserMetadata","attr");

        //这是手动获取数据库中的 json 字符串 并转成 元数据，方便使用
        info.setMetadata(jsonToMetadata(detail.getMetadata()));
        info.setUserMetadata(jsonToMetadata(detail.getUserMetadata()));
        info.setThMetadata(jsonToMetadata(detail.getThMetadata()));
        info.setThUserMetadata(jsonToMetadata(detail.getThUserMetadata()));
        //这是手动获取数据库中的 json 字符串 并转成 附加属性字典，方便使用
        info.setAttr(jsonToDict(detail.getAttr()));
        return info;
    }

    /**
     * 根据 url 删除文件信息
     */
    @Override
    public boolean delete(String url) {
        fileService.remove(new LambdaQueryWrapper<StorageFileEntity>().eq(StorageFileEntity::getUrl,url));
        return true;
    }

    /**
     * 将指定值转换成 json 字符串
     */
    public String valueToJson(Object value) throws JsonProcessingException {
        if (value == null) return null;
        return objectMapper.writeValueAsString(value);
    }

    /**
     * 将 json 字符串转换成元数据对象
     */
    public Map<String, String> jsonToMetadata(String json) throws JsonProcessingException {

        if (StrUtil.isBlank(json)) {
            return null;
        }else{
            return objectMapper.readValue(json,new TypeReference<Map<String, String>>() {});
        }
    }

    /**
     * 将 json 字符串转换成字典对象
     */
    public Dict jsonToDict(String json) throws JsonProcessingException {
        if (StrUtil.isBlank(json)) return null;
        return objectMapper.readValue(json,Dict.class);
    }

}
