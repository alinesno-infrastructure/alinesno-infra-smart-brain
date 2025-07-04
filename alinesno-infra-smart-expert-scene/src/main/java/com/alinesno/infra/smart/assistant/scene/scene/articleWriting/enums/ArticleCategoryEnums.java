package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ArticleCategoryEnums {
    OFFICE("office", "fa-solid fa-building", "办公必备"),
    MARKETING("marketing", "fa-solid fa-bullhorn", "商业营销"),
    EDUCATION("education", "fa-solid fa-book", "学习教育"),
    MEDIA("media", "fa-solid fa-camera", "媒体创作"),
    ART("art", "fa-solid fa-paint-brush", "文学艺术"),
    GOVERNMENT("government", "fa-solid fa-gavel", "机关公文"),
    REWRITE("rewrite", "fa-solid fa-pencil", "改写润色");

    private final String code;
    private final String icon;
    private final String label;

    // 返回List<Map<String , String>>列表
     public static List<Map<String, String>> getAllCategories() {
          return List.of(
                 Map.of("code", OFFICE.code, "icon", OFFICE.icon, "label", OFFICE.label),
                 Map.of("code", MARKETING.code, "icon", MARKETING.icon, "label", MARKETING.label),
                 Map.of("code", EDUCATION.code, "icon", EDUCATION.icon, "label", EDUCATION.label),
                 Map.of("code", MEDIA.code, "icon", MEDIA.icon, "label", MEDIA.label) ,
                 Map.of("code", ART.code, "icon", ART.icon, "label", ART.label),
                 Map.of("code", GOVERNMENT.code, "icon", GOVERNMENT.icon, "label", GOVERNMENT.label),
                 Map.of("code", REWRITE.code, "icon", REWRITE.icon, "label", REWRITE.label)
          ) ;
     }

     // 通过code获取到对应的ArticleCategoryEnums
     public static ArticleCategoryEnums getByCode(String code) {
         for (ArticleCategoryEnums value : values()) {
             if (value.code.equals(code)) {
                 return value;
             }
         }
         return null;
     }
}    