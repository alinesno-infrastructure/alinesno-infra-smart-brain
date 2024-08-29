package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author LuoXiaoDong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("api_record")
@Table(comment = "操作日志记录")
public class ApiRecordEntity extends InfraBaseEntity {

	/**
	 * 日志主键
	 */
	@TableField
	@ColumnType(value = MySqlTypeConstant.BIGINT, length = 20)
	@ColumnComment("日志主键")
	private Long operId;

	/**
	 * 租户编号
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 20)
	@ColumnComment("租户编号")
	private String tenantId;

	/**
	 * 操作模块
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 50)
	@ColumnComment("模块标题")
	private String title;

	/**
	 * 业务类型（0其它 1新增 2修改 3删除）
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.INT, length = 2)
	@ColumnComment("业务类型（0其它 1新增 2修改 3删除）")
	private Integer businessType;

	/**
	 * 请求方法
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 100)
	@ColumnComment("方法名称")
	private String method;

	/**
	 * 请求方式
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 10)
	@ColumnComment("请求方式")
	private String requestMethod;

	/**
	 * 操作类别（0其它 1后台用户 2手机端用户）
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.INT, length = 1)
	@ColumnComment("操作类别（0其它 1后台用户 2手机端用户）")
	private Integer operatorType;

	/**
	 * 操作人员
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 50)
	@ColumnComment("操作人员")
	private String operName;

	/**
	 * 部门名称
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 50)
	@ColumnComment("部门名称")
	private String deptName;

	/**
	 * 请求url
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 255)
	@ColumnComment("请求URL")
	private String operUrl;

	/**
	 * 操作地址
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 128)
	@ColumnComment("主机地址")
	private String operIp;

	/**
	 * 操作地点
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 255)
	@ColumnComment("操作地点")
	private String operLocation;

	/**
	 * 请求参数
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 2000)
	@ColumnComment("请求参数")
	private String operParam;

	/**
	 * 返回参数
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 2000)
	@ColumnComment("返回参数")
	private String jsonResult;

	/**
	 * 操作状态（0正常 1异常）
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.INT, length = 1)
	@ColumnComment("操作状态（0正常 1异常）")
	private Integer status;

	/**
	 * 错误消息
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.VARCHAR, length = 2000)
	@ColumnComment("错误消息")
	private String errorMsg;

	/**
	 * 操作时间
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.DATETIME)
	@ColumnComment("操作时间")
	private Date operTime;

	/**
	 * 消耗时间
	 */
	@TableField
	@ColumnType(value =MySqlTypeConstant.BIGINT, length = 20)
	@ColumnComment("消耗时间")
	private Long costTime;

}
