package com.alinesno.infra.smart.assistant.scene.scene.longtext.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.TaskProgressDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.mapper.LongTextTaskMapper;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTaskService;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.LongTextTaskEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用助手任务服务实现类
 */
@Slf4j
@Service
public class LongTextTaskServiceImpl extends IBaseServiceImpl<LongTextTaskEntity, LongTextTaskMapper> implements ILongTextTaskService {

    @Autowired
    private IChapterService chapterService ;

//    @Override
//    public List<LongTextTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId) {
//
//        page.setPageNum(0);
//        page.setPageSize(20);
//
//        Page<LongTextTaskEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());
//
//        LambdaQueryWrapper<LongTextTaskEntity> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(LongTextTaskEntity::getOrgId, query.getOrgId()) ;
//        wrapper.eq(LongTextTaskEntity::getSceneId, sceneId);
//        wrapper.orderByDesc(LongTextTaskEntity::getAddTime) ;
//
//        pageBean = page(pageBean, wrapper);
//
//        List<LongTextTaskEntity> list = pageBean.getRecords();
//
//        // 查询出第一章节的内容，并以此内容为描述内容
//        for (LongTextTaskEntity entity : list) {
//            LambdaQueryWrapper<ChapterEntity>  lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
//            lambdaQueryWrapper.eq(ChapterEntity::getTaskId, entity.getId()) ;
//            lambdaQueryWrapper.eq(ChapterEntity::getSceneId, sceneId) ;
//
//            List<ChapterEntity> chapterEntities = chapterService.list(lambdaQueryWrapper) ;
//            if (chapterEntities != null && !chapterEntities.isEmpty()) {
//                entity.setTaskDescription(chapterEntities.get(0).getContent()); // 这样处理可能第1个的内容为空
//            }else{
//                // 没有描述时描述长一些，并且人性化一些
//                String noDesc = "当前还没有执行任务，暂时无任务描述。";
//                entity.setTaskDescription(noDesc);
//            }
//
//        }
//
//        return list ;
//    }

    @Override
    public List<LongTextTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query, Long sceneId) {
        page.setPageNum(0);
        page.setPageSize(20);

        Page<LongTextTaskEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<LongTextTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LongTextTaskEntity::getOrgId, query.getOrgId());
        wrapper.eq(LongTextTaskEntity::getSceneId, sceneId);
        wrapper.orderByDesc(LongTextTaskEntity::getAddTime);

        pageBean = page(pageBean, wrapper);
        List<LongTextTaskEntity> list = pageBean.getRecords();

        if (list.isEmpty()) {
            return list;
        }

        // 收集所有任务ID
        List<Long> taskIds = list.stream().map(LongTextTaskEntity::getId).collect(Collectors.toList());

        // 批量查询这些任务的第一章节内容
        LambdaQueryWrapper<ChapterEntity> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.in(ChapterEntity::getTaskId, taskIds);
        chapterQuery.eq(ChapterEntity::getSceneId, sceneId);
        chapterQuery.isNotNull(ChapterEntity::getContent);  // 确保内容不为空
        chapterQuery.select(Arrays.asList(
                ChapterEntity::getTaskId,
                ChapterEntity::getContent
        ));

        // 按任务ID分组，只取每个任务的第一条记录
        Map<Long, String> taskContentMap = chapterService.list(chapterQuery).stream()
                .collect(Collectors.toMap(
                        ChapterEntity::getTaskId,
                        ChapterEntity::getContent,
                        (existing, replacement) -> existing  // 如果有多条，保留第一条
                ));

        // 设置任务描述
        String noDesc = "当前还没有执行任务，暂时无任务描述。";
        for (LongTextTaskEntity entity : list) {
            String content = taskContentMap.get(entity.getId());
            entity.setTaskDescription(content != null ? content : noDesc);
        }

        return list;
    }

    @Override
    public TaskProgressDto getProgress(Long taskId) {
        return null;
    }

}