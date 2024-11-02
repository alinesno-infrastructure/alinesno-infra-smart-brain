package com.alinesno.infra.smart.assistant.template.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 存储文件实体类
 * </p>
 * <p>
 * 该实体类用于存储文件的相关信息，包括DFS组名、下载次数、下载密码、过期日期、文件扩展名、文件标识、文件名称、云存储文件名称、文件长度、文件URL、文件源、是否公开、保存类型、阿里云链接、本地磁盘链接、本地磁盘地址、FastDFS链接、PaxosSURLRe链接、七牛链接、文件bucket、文件MD5、接口密钥、URLBFS和URLMongoDB等。
 * </p>
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StorageFileDto extends BaseDto {

    private String icon ;
    private String templateName;
    private String templateDesc;
    private String templateKey;
    private Integer callCount;
    private String templateParams;
    private Long storageFileId;
    private String templateType;

}
