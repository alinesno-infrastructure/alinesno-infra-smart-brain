//package com.alinesno.infra.smart.brain.entity;
//
//import cn.afterturn.easypoi.excel.annotation.Excel;
//import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
//import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
//import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
//import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//
///**
// * Prompt指令集实体类
// *
// * @author luoxiaodong
// * @version 1.0.0
// */
//@NoArgsConstructor
//@EqualsAndHashCode(callSuper = true)
//@Data
//@TableName("prompt_posts")
//@TableComment("Prompt指令实体类")
//public class PromptPostsEntity extends InfraBaseEntity {
//
//	/**
//	 * 数据来源接口(即跟prompt结合的数据源)
// 	 */
//	@Excel(name="数据来源接口(即跟prompt结合的数据源)")
//	@TableField("data_source_api")
//	@ColumnType(length=128)
//	@ColumnComment("数据来源接口(即跟prompt结合的数据源)")
//	private String dataSourceApi ;
//
//	/**
//	 * 指令使用次数
//	 */
//	@Excel(name="使用次数")
//	@TableField("use_count")
//	@ColumnType(length=5)
//	@ColumnComment("指令使用次数")
//	private Long useCount;
//
//	/**
//	 * 指令管理员
//	 */
//	@Excel(name="指令管理员")
//	@TableField("prompt_author")
//	@ColumnType(length=64)
//	@ColumnComment("指令管理员")
//	private String promptAuthor;
//
//	/**
//	 * 指令内容
//	 */
//	@Excel(name="指令内容")
//	@TableField("prompt_content")
//	@ColumnType(value = MySqlTypeConstant.LONGTEXT)
//	@ColumnComment("指令内容")
//	private String promptContent;
//
//	/**
//	 * 指令名称
//	 */
//	@Excel(name="指令名称")
//	@TableField("prompt_name")
//	@ColumnType(length=64)
//	@ColumnComment("指令名称")
//	private String promptName;
//
//	/**
//	 * 指令密钥
//	 */
//	@Excel(name="指令密钥")
//	@TableField("prompt_password")
//	@ColumnType(length=16)
//	@ColumnComment("指令密钥")
//	private String promptPassword;
//
//	/**
//	 * 指令状态
//	 */
//	@Excel(name="指令状态")
//	@TableField("prompt_status")
//	@ColumnType(length=2)
//	@ColumnComment("指令状态")
//	private Long promptStatus;
//
//	/**
//	 * 指令标题
//	 */
//	@Excel(name="指令标题")
//	@TableField("prompt_title")
//	@ColumnType(length=64)
//	@ColumnComment("指令标题")
//	private String promptTitle;
//
//	/**
//	 * 指令类型
//	 */
//	@Excel(name="指令类型")
//	@TableField("prompt_type")
//	@ColumnType(length=64)
//	@ColumnComment("指令类型")
//	private String promptType;
//
//	/**
//	 * 对外
//	 */
//	@Excel(name="对外")
//	@TableField("promptId")
//	@ColumnType(length=10)
//	@ColumnComment("对外")
//	private String promptId;
//
//	@TableField
//	@ColumnComment("最大Token")
//	@ColumnType
//	private int maxToken ;
//
//	@TableField
//	@ColumnComment("模型名称")
//	@ColumnType
//	private String modelName ;
//
//	@TableField
//	@ColumnComment("冷静度")
//	@ColumnType
//	private int temperature ;
//
//	@TableField
//	@ColumnComment("概率范围")
//	@ColumnType
//	private int topP ;
//
//}
