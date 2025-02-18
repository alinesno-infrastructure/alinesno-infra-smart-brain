package com.alinesno.infra.base.search.gateway.utils.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    private static final String[] COLORS = {
        "#409EFF", // Brand Color
        "#67C23A", // Success
        "#E6A23C", // Warning
        "#F56C6C", // Danger
        "#909399"  // Info
    };

    public static List<ItemObject> generateItemObjects(List<String> inputList) {
        List<ItemObject> dataObjects = new ArrayList<>();
        Random random = new Random();

        for (String item : inputList) {
            ItemObject dataObject = new ItemObject();
            dataObject.setValue(random.nextInt(1000) + 1); // 随机生成1到1000之间的整数
            dataObject.setItemStyle(new ItemStyle());
            dataObject.getItemStyle().setColor(COLORS[0]) ; // random.nextInt(COLORS.length)]); // 随机选择颜色
            dataObjects.add(dataObject);
        }

        return dataObjects;
    }

    @Data
    public static class ItemObject {
        private Integer value;
        private ItemStyle itemStyle;
    }

    @Data
    public static class ItemStyle {
        private String color;
    }
}
