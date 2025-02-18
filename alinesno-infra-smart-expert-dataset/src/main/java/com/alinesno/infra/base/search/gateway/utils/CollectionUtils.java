package com.alinesno.infra.base.search.gateway.utils;

import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Random;

public class CollectionUtils {

    /**
     * // TODO 待优化
     * 确定唯一值，不重复
     */
    public static String getCollectionName() {

        String collectionName = generateRandomString(10);

        IVectorDatasetService vectorDatasetService = SpringUtils.getBean(IVectorDatasetService.class);
        LambdaQueryWrapper<VectorDatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VectorDatasetEntity::getCollectionName, collectionName);
        long count = vectorDatasetService.count(queryWrapper);

        if (count > 0) {
            return getCollectionName();
        }

        return collectionName;
    }


    // TODO 待处理确认当前为系统唯一值
    public static String generateRandomString(int length) {

        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();
    }
}