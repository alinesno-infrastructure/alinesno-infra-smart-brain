package com.alinesno.infra.smart.assistant.screen.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Data // Lombok 注解用于生成 getter/setter, equals/hashCode, toString 等方法
public class ChapterEditorDto {

    @Min(value = 1, message = "ID 必须是正整数")
    private long id;

    @NotNull(message = "编辑者不能为空")
    @Size(min = 1, max = 255, message = "编辑者的长度必须在1到255之间")
    private List<String> editors;

    @NotNull(message = "类型不能为空")
    @Size(min = 1, max = 50, message = "类型的长度必须在1到50之间")
    private String type;

    // Method to concatenate the editors into a single string separated by commas
    public String getEditorsAsString() {
        if (editors == null || editors.isEmpty()) {
            return "";
        }
        return editors.stream()
                .filter(editor -> editor != null && !editor.trim().isEmpty())
                .collect(Collectors.joining(","));
    }
}