package com.alinesno.infra.smart.assistant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行业角色实体类，用于表示不同行业中的角色信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("industry_role") // MyBatis-Plus 表名注解
public class IndustryRoleEntity extends InfraBaseEntity {

    @TableField("role_avatar") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("角色名称")
    private String roleAvatar; // 角色头像

    @TableField("role_name") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("角色名称")
    private String roleName; // 角色名称

    @TableField("backstory")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("角色背景")
    private String backstory ; // 角色背景

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 32)
    @ColumnComment("关联的ChainID")
    @TableField("chain_id") // MyBatis-Plus 字段注解
    private String chainId; // 其他角色相关字段

    @TableField("greeting")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 512)
    @ColumnComment("开场白")
    private String greeting ; // 开场白

    @TableField("greeting_question")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 512)
    @ColumnComment("开场白问题")
    private String greetingQuestion ; // 开场白问题

    // 历史对话数量
    @TableField("history_count")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("历史对话数量，默认为0")
    private Long historyCount = 0L ;

    @TableField("ask_human_help")
    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @ColumnComment("是否需要人类帮助")
    private boolean askHumanHelp = false ;

    /**
     * 1. 单角色：自己单独完成一个聊天，可流式输出或者同步输出
     * 2. 协作角色：与其它角色协作才可以完成一个工作，可流式输出或者同步输出
     * 3. 场景角色：与其它角色协作才可以完成一个工作，只能同步输出
     */
    @TableField("role_type")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("角色类型")
    private String roleType ; // 角色类型

    @TableField("industry_catalog") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("所属行业")
    private Long industryCatalog ; // 所属行业

    @TableField("responsibilities") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("角色职责描述")
    private String responsibilities; // 角色职责描述

    @TableField("role_level") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("角色等级")
    private String roleLevel; // 角色等级

    @TableField("script_type")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("执行类型(脚本script|流程flow)")
    private String scriptType ; // 执行类型(脚本script|流程flow|默认default)

    @TableField("other_attributes") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("其他角色相关字段")
    private String otherAttributes; // 其他角色相关字段

	@Excel(name="指令内容")
	@TableField("prompt_content")
	@ColumnType(value = MySqlTypeConstant.LONGTEXT)
	@ColumnComment("指令内容")
	private String promptContent;

    @Excel(name="会议次数")
    @TableField("chat_count")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @ColumnComment("会话次数")
    private Long chatCount = 0L ;

    // 推荐角色(用于Hero推荐)
    @TableField("recommended_role")
    @ColumnType(value = MySqlTypeConstant.SMALLINT, length = 1)
    @ColumnComment("是否是推荐")
    private boolean hasRecommended ;

    @TableField("has_sale")
    @ColumnType(value = MySqlTypeConstant.SMALLINT, length = 1)
    @ColumnComment("是否销售(1销售|0不可销售|9不可转售)")
    private int hasSale = 0 ;

    @TableField("sale_from_role_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("销售来源角色ID")
    private Long saleFromRoleId = 0L ;

    // 知识库ID列表，用于关联相关知识数据
    @TableField("knowledge_base_ids")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "知识库ID列表")
    private String knowledgeBaseIds;

    @TableField("selection_tools_data")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "工具ID列表")
    private String selectionToolsData;

    // 是否启用长期记忆功能
    @TableField("long_term_memory_enabled")
    @Column(type = MySqlTypeConstant.INT , length = 1 , isNull = true , comment = "是否启用长期记忆功能")
    private boolean longTermMemoryEnabled;

    // 是否启用语音输入状态
    @TableField("voice_input_status")
    @Column(type = MySqlTypeConstant.INT , length = 1 , isNull = true , comment = "是否启用语音输入状态")
    private boolean voiceInputStatus;

    @TableField("guess_what_you_ask_status")
    @Column(type = MySqlTypeConstant.INT , length = 1 , isNull = true , comment = "用户问题建议")
    private boolean guessWhatYouAskStatus ;

    // 语音播放
    @TableField("voice_play_status")
    @Column(type = MySqlTypeConstant.INT , length = 1 , isNull = true , comment = "语音播放")
    private boolean voicePlayStatus ;

    @TableField("upload_status")
    @Column(type = MySqlTypeConstant.INT , length = 1 , isNull = true , comment = "上传状态")
    private boolean uploadStatus ;

    // 欢迎配置
    @TableField("welcome_config_status")
    @Column(type = MySqlTypeConstant.INT , length = 1 , isNull = true , comment = "欢迎配置")
    private boolean welcomeConfigStatus ;

    // 欢迎配置数据
    @TableField("welcome_config_data")
    @Column(type = MySqlTypeConstant.TEXT , isNull = true , comment = "欢迎配置数据")
    private String welcomeConfigData ;

    // 模型
    @TableField("model_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, isNull = true , comment = "模型")
    private Long modelId;

    // 模型配置相关信息
    @TableField("model_config")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "模型配置相关信息")
    private String modelConfig;

    // 语音输入相关数据配置
    @TableField("voice_input_data")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "语音输入相关数据配置")
    private String voiceInputData;

    // 猜测用户问题相关数据配置
    @TableField("guess_what_you_ask_data")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "猜测用户问题相关数据配置")
    private String guessWhatYouAskData;

    // 语音播放相关数据配置
    @TableField("voice_play_data")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "语音播放相关数据配置")
    private String voicePlayData;

    // 数据集搜索配置
    @TableField("dataset_search_config")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "数据集搜索配置")
    private String datasetSearchConfig;

    // 是否文件上传配置
    @TableField("upload_data")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, isNull = true , comment = "文件上传配置")
    private String uploadData;

    // --->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 角色脚本 ----------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // 执行脚本
    @TableField("execute_script")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("执行脚本")
    private String executeScript ;

    // 审核脚本
    @TableField("audit_script")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("审核脚本")
    private String auditScript ;

    // 功能回调脚本
    @TableField("function_callback_script")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("功能回调脚本")
    private String functionCallbackScript ;

    // --->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Agent(DeepSearch)规划设计 ----------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // Agent循环次数
    @TableField("agent_loop_count")
    @ColumnType(value = MySqlTypeConstant.INT, length = 11)
    @ColumnComment("Agent循环次数")
    private Integer agentLoopCount ;

    // Agent任务规划条数
    @TableField("agent_task_plan_count")
    @ColumnType(value = MySqlTypeConstant.INT, length = 11)
    @ColumnComment("Agent任务规划条数")
    private Integer agentTaskPlanCount ;

    // 是否需要格式化内容
    @TableField("output_file_format_status")
    @ColumnType(value = MySqlTypeConstant.INT, length = 1)
    @ColumnComment("是否需要格式化内容")
    private boolean outputFileFormatStatus;

    // 配置输出文件格式
    @TableField("output_file_format_data")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("配置输出文件格式(docx/excel/md/html)")
    private String outputFileFormatData ;

}