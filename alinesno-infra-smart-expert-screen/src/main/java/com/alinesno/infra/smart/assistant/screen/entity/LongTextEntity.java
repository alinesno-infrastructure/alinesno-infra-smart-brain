package com.alinesno.infra.smart.assistant.screen.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景属性
 */
@EqualsAndHashCode(callSuper = true)
@TableName("longtext_content")
@Data
public class LongTextEntity extends InfraBaseEntity {

    // 文本内容、所属章节、编写人员、状态（草稿|正式）
    @Column(name = "content", type = MySqlTypeConstant.LONGTEXT, comment = "文本内容")
    private String content;

    @Column(name = "chapter_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "章节ID")
    private Long chapterId;

    @Column(name = "editor", type = MySqlTypeConstant.BIGINT , length = 32, comment = "编写人员")
    private Long editor;

}
