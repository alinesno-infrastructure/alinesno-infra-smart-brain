package com.alinesno.infra.smart.assistant.publish.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import com.alinesno.infra.smart.assistant.publish.enums.ChannelListEnums;
import com.alinesno.infra.smart.assistant.publish.utils.EncryptionUtils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 发布渠道数据传输对象，用于在不同层之间传输发布渠道信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelPublishDTO extends BaseDto {

    @Min(value = 1, message = "角色ID不能为空")
    private long roleId; // 角色ID，标识与该发布渠道相关联的角色

    @NotBlank(message = "名称不能为空")
    private String shareName; // 发布渠道的名称
    private String description; // 发布渠道的描述信息
    private String iconClass; // 图标类名，用于表示发布渠道的图标

    @NotBlank(message = "参数键不能为空")
    private String paramKey; // 参数键，用于唯一标识发布渠道的配置参数
    private int hasConfigured = 0; // 是否已配置的标志，1表示已配置，0表示未配置
    private int expireType = 7; // 过期时间类型，0: 无限，1: 7天，2: 30天，3: 180天，4: 自定义
    private Date expireTime; // 自定义过期时间
    private int qpm = 100; // QPM（每分钟查询率）
    private String appId; // 微信公众号 appId
    private String secret; // 微信公众号 Secret
    private String token; // 微信公众号 Token
    private String aesKey; // 微信公众号 AES Key
    private boolean hasAuth = false ; // 是否进行身份验证
    private String authPassword; // 身份验证密码
    private String apiKey; // API 接口的 APIKey

    /**
     * 转换为实体对象
     * @return
     */
    @SneakyThrows
    public ChannelPublishEntity toEntity() {

        ChannelListEnums channelListEnums = ChannelListEnums.getByParamKey(paramKey);
        Assert.notNull(channelListEnums , "paramKey is not found in ChannelListEnums") ;

        ChannelPublishEntity entity = new ChannelPublishEntity();
        BeanUtils.copyProperties(this , entity) ;

        entity.setRoleId(roleId);
        entity.setApiKey(apiKey);
        entity.setName(shareName);
        entity.setDescription(description);
        entity.setIconClass(channelListEnums.getIconClass());
        entity.setParamKey(paramKey);
        entity.setHasConfigured(hasConfigured);

        Map<String, Object> config = new HashMap<>();
        config.put("expireType", expireType);
        config.put("qpm", qpm);

        if (expireTime != null) {
            config.put("expireTime", expireTime);
        }
        if (appId != null && !appId.isEmpty()) {
            String aesEncrypted = EncryptionUtils.aesEncrypt(appId, EncryptionUtils.DEFAULT_KEY);
            config.put("appId", aesEncrypted);
        }
        if (secret != null && !secret.isEmpty()) {
            String aesEncrypted = EncryptionUtils.aesEncrypt(secret, EncryptionUtils.DEFAULT_KEY);
            config.put("secret", aesEncrypted);
        }
        if (token != null && !token.isEmpty()) {
            String aesEncrypted = EncryptionUtils.aesEncrypt(token, EncryptionUtils.DEFAULT_KEY);
            config.put("token", aesEncrypted);
        }
        if (aesKey != null && !aesKey.isEmpty()) {
            String aesEncrypted = EncryptionUtils.aesEncrypt(aesKey, EncryptionUtils.DEFAULT_KEY);
            config.put("aesKey", aesEncrypted);
        }
        config.put("hasAuth", hasAuth);
        if (authPassword != null && !authPassword.isEmpty()) {
            String aesEncrypted = EncryptionUtils.aesEncrypt(authPassword, EncryptionUtils.DEFAULT_KEY);
            config.put("authPassword", aesEncrypted);
        }

        entity.setConfigMap(JSONObject.toJSONString(config));

        return entity;
    }

    /**
     * 从实体对象转换为DTO对象
     * @param entity 发布渠道实体对象
     * @return 发布渠道DTO对象
     */
    @SneakyThrows
    public static ChannelPublishDTO toDto(ChannelPublishEntity entity) {
        ChannelPublishDTO dto = new ChannelPublishDTO();
        dto.setRoleId(entity.getRoleId());
        dto.setShareName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIconClass(entity.getIconClass());
        dto.setParamKey(entity.getParamKey());
        dto.setHasConfigured(entity.getHasConfigured());
        dto.setApiKey(entity.getApiKey());

        String configMapStr = entity.getConfigMap();
        if (configMapStr != null && !configMapStr.isEmpty()) {
            // 指定泛型类型参数来消除警告
            Map<String, Object> configMap = JSONObject.parseObject(configMapStr, new TypeReference<>() {});
            if (configMap.containsKey("expireType")) {
                dto.setExpireType(Integer.parseInt(configMap.get("expireType").toString()));
            }
            if (configMap.containsKey("expireTime")) {
                dto.setExpireTime((Date) configMap.get("expireTime"));
            }
            if (configMap.containsKey("qpm")) {
                dto.setQpm(Integer.parseInt(configMap.get("qpm").toString()));
            }
            if (configMap.containsKey("appId")) {
                String appId = configMap.get("appId").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(appId, EncryptionUtils.DEFAULT_KEY);
                dto.setAppId(aesEncrypted) ;
            }
            if (configMap.containsKey("secret")) {
                String secret = configMap.get("secret").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(secret, EncryptionUtils.DEFAULT_KEY);
                dto.setSecret(aesEncrypted) ;
            }
            if (configMap.containsKey("token")) {
                String token = configMap.get("token").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(token, EncryptionUtils.DEFAULT_KEY);
                dto.setToken(aesEncrypted) ;
            }
            if (configMap.containsKey("aesKey")) {
                String aesKey = configMap.get("aesKey").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(aesKey, EncryptionUtils.DEFAULT_KEY);
                dto.setAesKey(aesEncrypted) ;
            }
            if (configMap.containsKey("hasAuth")) {
                dto.setHasAuth(Boolean.parseBoolean(configMap.get("hasAuth").toString()));
            }
            if (configMap.containsKey("authPassword")) {
                String authPassword = configMap.get("authPassword").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(authPassword, EncryptionUtils.DEFAULT_KEY);
                dto.setAuthPassword(aesEncrypted)  ;
            }

        }

        return dto;
    }

    /**
     * 从实体对象转换为DTO对象
     * @param entity 发布渠道实体对象
     * @return 发布渠道DTO对象
     */
    @SneakyThrows
    public static Map<String , Object> toMap(ChannelPublishEntity entity) {

        String configMapStr = entity.getConfigMap();

        if (configMapStr != null && !configMapStr.isEmpty()) {
            // 指定泛型类型参数来消除警告
            Map<String, Object> configMap = JSONObject.parseObject(configMapStr, new TypeReference<>() {});

            configMap.put("id", entity.getId()) ;
            configMap.put("roleId", entity.getRoleId()) ;
            configMap.put("shareName", entity.getName()) ;
            configMap.put("description", entity.getDescription()) ;
            configMap.put("iconClass", entity.getIconClass()) ;
            configMap.put("paramKey", entity.getParamKey()) ;
            configMap.put("apiKey", entity.getApiKey()) ;
            configMap.put("isConfigured", entity.getHasConfigured()) ;

            if (configMap.containsKey("appId")) {
                String appId = configMap.get("appId").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(appId, EncryptionUtils.DEFAULT_KEY);
                configMap.put("appId", aesEncrypted);
            }
            if (configMap.containsKey("secret")) {
                String secret = configMap.get("secret").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(secret, EncryptionUtils.DEFAULT_KEY);
                configMap.put("secret", aesEncrypted);
            }
            if (configMap.containsKey("token")) {
                String token = configMap.get("token").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(token, EncryptionUtils.DEFAULT_KEY);
                configMap.put("token", aesEncrypted);
            }
            if (configMap.containsKey("aesKey")) {
                String aesKey = configMap.get("aesKey").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(aesKey, EncryptionUtils.DEFAULT_KEY);
                configMap.put("aesKey", aesEncrypted);
            }
            if (configMap.containsKey("authPassword")) {
                String authPassword = configMap.get("authPassword").toString();
                String aesEncrypted = EncryptionUtils.aesDecrypt(authPassword, EncryptionUtils.DEFAULT_KEY);
                configMap.put("authPassword", aesEncrypted);
            }

            return configMap;
        }

        return new HashMap<>();
    }
}