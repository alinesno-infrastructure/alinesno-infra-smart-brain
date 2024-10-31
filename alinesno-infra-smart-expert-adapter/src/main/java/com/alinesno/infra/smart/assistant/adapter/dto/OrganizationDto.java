package com.alinesno.infra.smart.assistant.adapter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 组织信息数据传输对象类
 * 用于序列化和反序列化组织相关数据，便于网络传输。
 * 包含了组织的基本信息、地理位置、联系方式以及状态等。
 *
 * @author WeiXiaoJin
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrganizationDto extends BaseDto {

    private int type; // 组织创建类型(加入1|创建0)
    private String joinDoorplateNumber ; // 加入的组织ID
    private String orgName;      // 组织名称
    private String remark;       // 备注
    private String logoUrl;      // LOGO图片地址
    private String doorplateNumber; // 门牌号
    private String organizationId; // 组织ID
    private String subdomain;    // 子域名
    private String workLocation; // 工作地点
    private Date startTime;      // 开始时间
    private Date endTime;        // 结束时间
    private String orgStatus;    // 组织状态
    private String cityId;       // 城市ID
    private String provinceId;   // 省份ID
    private String orgAddress;   // 组织地址
    private String orgPhone;     // 组织电话
    private String systemInner;  // 内部系统标识
    private long memberCount;    // 成员数量
    private int accountOrgType;  // 账户组织类型

}

