package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 考生信息实体类
 * 对应数据库中的 examinee_info 表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("examinee_info")
public class ExamineeEntity extends InfraBaseEntity {

    /**
     * 考生唯一编号（业务ID）
     */
    @TableField("examinee_id")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "考试编号")
    private String examineeId;

    /**
     * 考生姓名
     */
    @TableField("name")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 50, comment = "姓名")
    private String name;

    /**
     * 性别（0-未知 1-男 2-女）
     */
    @TableField("gender")
    @Column(type = MySqlTypeConstant.INT, comment = "性别")
    private Integer gender = 0;

    /**
     * 身份证号码
     */
    @TableField("id_card_number")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 18, comment = "身份证号码")
    private String idCardNumber;

    /**
     * 手机号码
     */
    @TableField("phone")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 20, comment = "手机号码")
    private String phone;

    /**
     * 电子邮箱
     */
    @TableField("email")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 100, comment = "电子邮箱")
    private String email;

    /**
     * 考生照片URL
     */
    @TableField("photo_url")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "照片URL")
    private String photoUrl;

    /**
     * 所属机构/学校/单位
     */
    @TableField("organization")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "所属机构/学校/单位")
    private String organization;

    /**
     * 考生状态（0-未激活 1-正常 2-禁用）
     */
    @TableField("status")
    @Column(type = MySqlTypeConstant.INT, comment = "考生状态")
    private Integer status = 1;

    /**
     * 备注信息
     */
    @TableField("remark")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "备注信息")
    private String remark;
}