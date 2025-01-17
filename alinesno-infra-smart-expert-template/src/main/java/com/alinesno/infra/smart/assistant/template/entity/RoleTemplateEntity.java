package com.alinesno.infra.smart.assistant.template.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目模块实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("role_template")
@TableComment("插件管理实体")
public class RoleTemplateEntity extends InfraBaseEntity {

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
    @ColumnComment("roleScriptType")
    @TableField("role_script_type")
    private String roleScriptType;

    @TableField("greeting")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 512)
    @ColumnComment("开场白")
    private String greeting ; // 开场白

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

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = true , comment = "知识库ID")
    private String knowledgeId ;

    // --->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 角色脚本 ----------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // 感知脚本
    @TableField("perception_script")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("感知脚本")
    private String perceptionScript;

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

    // 角色工具类
    @TableField("tools")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("角色工具类")
    private String tools ;

}