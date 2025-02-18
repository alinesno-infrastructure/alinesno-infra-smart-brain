package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

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
@TableName("storage_file")
@Data
public class StorageFileEntity extends InfraBaseEntity {

//    @TableId("id")
//    @Column(name = "id", type = MySqlTypeConstant.VARCHAR, length = 32, isKey = true, comment = "文件id")
//    private String id;

    @TableField("url")
    @Column(name = "url", type = MySqlTypeConstant.VARCHAR, length = 512, comment = "文件访问地址")
    private String url;

    @TableField("size")
    @Column(name = "size", type = MySqlTypeConstant.BIGINT, length = 20, comment = "文件大小，单位字节")
    private Long size;

    @TableField("filename")
    @Column(name = "filename", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "文件名称")
    private String filename;

    @TableField("original_filename")
    @Column(name = "original_filename", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "原始文件名")
    private String originalFilename;

    @TableField("base_path")
    @Column(name = "base_path", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "基础存储路径")
    private String basePath;

    @TableField("path")
    @Column(name = "path", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "存储路径")
    private String path;

    @TableField("ext")
    @Column(name = "ext", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文件扩展名")
    private String ext;

    @TableField("content_type")
    @Column(name = "content_type", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "MIME类型")
    private String contentType;

    @TableField("platform")
    @Column(name = "platform", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "存储平台")
    private String platform;

    @TableField("th_url")
    @Column(name = "th_url", type = MySqlTypeConstant.VARCHAR, length = 512, comment = "缩略图访问路径")
    private String thUrl;

    @TableField("th_filename")
    @Column(name = "th_filename", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "缩略图名称")
    private String thFilename;

    @TableField("th_size")
    @Column(name = "th_size", type = MySqlTypeConstant.BIGINT, length = 20, comment = "缩略图大小，单位字节")
    private Long thSize;

    @TableField("th_content_type")
    @Column(name = "th_content_type", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "缩略图MIME类型")
    private String thContentType;

    @TableField("object_id")
    @Column(name = "object_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文件所属对象id")
    private String objectId;

    @TableField("object_type")
    @Column(name = "object_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文件所属对象类型，例如用户头像，评价图片")
    private String objectType;

    @TableField("metadata")
    @Column(name = "metadata", type = MySqlTypeConstant.TEXT, comment = "文件元数据")
    private String metadata;

    @TableField("user_metadata")
    @Column(name = "user_metadata", type = MySqlTypeConstant.TEXT, comment = "文件用户元数据")
    private String userMetadata;

    @TableField("th_metadata")
    @Column(name = "th_metadata", type = MySqlTypeConstant.TEXT, comment = "缩略图元数据")
    private String thMetadata;

    @TableField("th_user_metadata")
    @Column(name = "th_user_metadata", type = MySqlTypeConstant.TEXT, comment = "缩略图用户元数据")
    private String thUserMetadata;

    @TableField("attr")
    @Column(name = "attr", type = MySqlTypeConstant.TEXT, comment = "附加属性")
    private String attr;

    @TableField("file_acl")
    @Column(name = "file_acl", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文件ACL")
    private String fileAcl;

    @TableField("th_file_acl")
    @Column(name = "th_file_acl", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "缩略图文件ACL")
    private String thFileAcl;

    @TableField("create_time")
    @Column(name = "create_time", type = MySqlTypeConstant.DATETIME, comment = "创建时间")
    private Date createTime;
}
