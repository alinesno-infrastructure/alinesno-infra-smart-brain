package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 排版配置Excel数据模型类
 * 用于存储界面排版场景中从Excel表格读取的数据
 * 包括排版名称、描述、配置、分组信息以及导入失败原因等
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentLayoutExcelData {

    /**
     * 排版名称
     */
    private String layoutName;

    /**
     * 排版描述
     */
    private String layoutDesc;

    /**
     * 排版配置内容
     */
    private String layoutConfig;

    /**
     * 所属分组ID
     */
    private Long groupId;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组描述
     */
    private String groupDesc;

    /**
     * 分组图标
     */
    private String groupIcon;

    /**
     * 失败原因
     * 包含错误信息和相关详情
     */
    private Map<String, String> failReason;

    /**
     * 快速创建失败记录
     * @param layoutName 排版名称
     * @param errorMsg 错误信息
     * @param details 错误详情
     * @return 包含失败信息的对象
     */
    public static ContentLayoutExcelData createFailedRecord(
            String layoutName,
            String errorMsg,
            Map<String, String> details) {

        ContentLayoutExcelData data = new ContentLayoutExcelData();
        data.setLayoutName(layoutName);
        details.put("error", errorMsg);
        data.setFailReason(details);
        return data;
    }

    /**
     * 快速创建失败记录（简化版）
     * @param layoutName 排版名称
     * @param errorMsg 错误信息
     * @return 包含失败信息的对象
     */
    public static ContentLayoutExcelData createFailedRecord(
            String layoutName,
            String errorMsg) {

        return createFailedRecord(layoutName, errorMsg, Map.of());
    }
}