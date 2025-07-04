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
    private boolean hasAuth = false; // 是否进行身份验证
    private String authPassword; // 身份验证密码
    private String apiKey; // API 接口的 APIKey
    private long agentStoreType; // 智能体商店类型

    private long llmModelId; // 场景的LLM 模型的智能体ID
    private long imageModelId; // 场景的图片模型的智能体ID
    private long sceneId; // 场景的智能体ID
    private long agentTypeId; // 场景的智能体类型ID
    private String sceneScope ; // 场景的智能体商店类型

    /**
     * 转换为实体对象
     * @return
     */
    @SneakyThrows
    public ChannelPublishEntity toEntity() {
        ChannelListEnums channelListEnums = ChannelListEnums.getByParamKey(paramKey);
        Assert.notNull(channelListEnums, "paramKey is not found in ChannelListEnums");

        ChannelPublishEntity entity = new ChannelPublishEntity();
        BeanUtils.copyProperties(this, entity);

        entity.setRoleId(roleId);
        entity.setApiKey(apiKey);
        entity.setName(shareName);
        entity.setDescription(description);
        entity.setIconClass(channelListEnums.getIconClass());
        entity.setParamKey(paramKey);
        entity.setHasConfigured(hasConfigured);

        Map<String, Object> config = buildConfigMap();
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
            Map<String, Object> configMap = JSONObject.parseObject(configMapStr, new TypeReference<>() {});
            setConfigToDto(dto, configMap, entity.getParamKey());
        }

        return dto;
    }

    /**
     * 从实体对象转换为Map对象
     * @param entity 发布渠道实体对象
     * @return 包含发布渠道信息的Map对象
     */
    @SneakyThrows
    public static Map<String, Object> toMap(ChannelPublishEntity entity) {
        String configMapStr = entity.getConfigMap();
        Map<String, Object> configMap = new HashMap<>();
        if (configMapStr != null && !configMapStr.isEmpty()) {
            configMap = JSONObject.parseObject(configMapStr, new TypeReference<>() {});
        }

        configMap.put("id", entity.getId());
        configMap.put("roleId", entity.getRoleId());
        configMap.put("shareName", entity.getName());
        configMap.put("description", entity.getDescription());
        configMap.put("iconClass", entity.getIconClass());
        configMap.put("paramKey", entity.getParamKey());
        configMap.put("apiKey", entity.getApiKey());
        configMap.put("isConfigured", entity.getHasConfigured());

        decryptConfigValues(configMap);

        if (entity.getParamKey().equals(ChannelListEnums.AIP_AGENT_SCENE.getParamKey())) {
            configMap.put("sceneId", configMap.get("sceneId"));
            configMap.put("agentTypeId", configMap.get("agentTypeId"));
            configMap.put("llmModelId", configMap.get("llmModelId"));
            configMap.put("imageModelId", configMap.get("imageModelId"));
            configMap.put("sceneScope", configMap.get("sceneScope"));
        }

        return configMap;
    }

    /**
     * 构建配置映射
     * @return 配置映射
     */
    private Map<String, Object> buildConfigMap() {
        Map<String, Object> config = new HashMap<>();
        config.put("expireType", expireType);
        config.put("qpm", qpm);

        if (expireTime != null) {
            config.put("expireTime", expireTime);
        }
        if (agentStoreType != 0) {
            config.put("agentStoreType", agentStoreType);
        }

        putEncryptedValue(config, "appId", appId);
        putEncryptedValue(config, "secret", secret);
        putEncryptedValue(config, "token", token);
        putEncryptedValue(config, "aesKey", aesKey);

        config.put("hasAuth", hasAuth);
        putEncryptedValue(config, "authPassword", authPassword);

        if (ChannelListEnums.getByParamKey(paramKey) == ChannelListEnums.AIP_AGENT_SCENE) {
            config.put("llmModelId", llmModelId);
            config.put("imageModelId", imageModelId);
            config.put("sceneId", sceneId);
            config.put("agentTypeId", agentTypeId);
            config.put("sceneScope", sceneScope);
        }

        return config;
    }

    /**
     * 将配置项设置到 DTO 对象中
     * @param dto 发布渠道 DTO 对象
     * @param configMap 配置映射
     * @param paramKey 参数键
     */
    private static void setConfigToDto(ChannelPublishDTO dto, Map<String, Object> configMap, String paramKey) {
        if (configMap.containsKey("expireType")) {
            dto.setExpireType(Integer.parseInt(configMap.get("expireType").toString()));
        }
        if (configMap.containsKey("agentStoreType")) {
            dto.setAgentStoreType((Long) configMap.get("agentStoreType"));
        }
        if (configMap.containsKey("expireTime")) {
            dto.setExpireTime((Date) configMap.get("expireTime"));
        }
        if (configMap.containsKey("qpm")) {
            dto.setQpm(Integer.parseInt(configMap.get("qpm").toString()));
        }

        setDecryptedValue(dto::setAppId, configMap, "appId");
        setDecryptedValue(dto::setSecret, configMap, "secret");
        setDecryptedValue(dto::setToken, configMap, "token");
        setDecryptedValue(dto::setAesKey, configMap, "aesKey");

        if (configMap.containsKey("hasAuth")) {
            dto.setHasAuth(Boolean.parseBoolean(configMap.get("hasAuth").toString()));
        }
        setDecryptedValue(dto::setAuthPassword, configMap, "authPassword");

        if (paramKey.equals(ChannelListEnums.AIP_AGENT_SCENE.getParamKey())) {
            dto.setSceneId((Long) configMap.get("sceneId"));
            dto.setAgentTypeId((Long) configMap.get("agentTypeId"));
            dto.setLlmModelId((Long) configMap.get("llmModelId"));
            dto.setImageModelId((Long) configMap.get("imageModelId"));
            dto.setSceneScope((String) configMap.get("sceneScope"));
        }
    }

    /**
     * 将加密值放入配置映射
     * @param config 配置映射
     * @param key 键
     * @param value 值
     */
    @SneakyThrows
    private void putEncryptedValue(Map<String, Object> config, String key, String value) {
        if (value != null && !value.isEmpty()) {
            config.put(key, EncryptionUtils.aesEncrypt(value, EncryptionUtils.DEFAULT_KEY));
        }
    }

    /**
     * 设置解密后的值
     * @param setter 设置方法
     * @param configMap 配置映射
     * @param key 键
     */
    @SneakyThrows
    private static void setDecryptedValue(java.util.function.Consumer<String> setter, Map<String, Object> configMap, String key) {
        if (configMap.containsKey(key)) {
            String encryptedValue = configMap.get(key).toString();
            String decryptedValue = EncryptionUtils.aesDecrypt(encryptedValue, EncryptionUtils.DEFAULT_KEY);
            setter.accept(decryptedValue);
        }
    }

    /**
     * 解密配置值
     * @param configMap 配置映射
     */
    private static void decryptConfigValues(Map<String, Object> configMap) {
        setDecryptedValue(value -> configMap.put("appId", value), configMap, "appId");
        setDecryptedValue(value -> configMap.put("secret", value), configMap, "secret");
        setDecryptedValue(value -> configMap.put("token", value), configMap, "token");
        setDecryptedValue(value -> configMap.put("aesKey", value), configMap, "aesKey");
        setDecryptedValue(value -> configMap.put("authPassword", value), configMap, "authPassword");
    }
}