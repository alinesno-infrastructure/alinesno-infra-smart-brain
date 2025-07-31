package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamSubmissionDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeImportDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeImportParam;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ImportResult;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamineeMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamineeService;
import com.alinesno.infra.smart.scene.entity.ExamineeEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考生ServiceImpl类
 */
@Slf4j
@Service
public class ExamineeServiceImpl extends IBaseServiceImpl<ExamineeEntity, ExamineeMapper> implements IExamineeService {

    /**
     * 初始化考生信息
     * 如果不存在，则添加，存在（通过examineeId来判断考生是否存在），则更新并返回
     * @param submission
     * @return
     */
    @Override
    public String initExaminee(ExamSubmissionDto submission) {
        log.debug("submission = {}", submission);

        LambdaQueryWrapper<ExamineeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamineeEntity::getExamineeId, submission.getExamineeId());

        ExamineeEntity examinee = getOne(queryWrapper);
        if (examinee == null) {

            examinee = new ExamineeEntity() ;
            examinee.setExamineeId(submission.getExamineeId());
            examinee.setName(submission.getName());

            this.save(examinee);
        }

        return String.valueOf(examinee.getId());
    }

    @Override
    public ImportResult importExaminees(List<ExamineeImportDTO> importList, Long sceneId, ExamineeImportParam importParam) {
        // 1. 数据校验
        if (importList == null || importList.isEmpty()) {
            throw new RpcServiceRuntimeException("导入数据不能为空");
        }

        // 2. 检查重复情况
        ImportResult result = checkDuplicates(importList, sceneId);
        if (result.hasDuplicates()) {
            return result;
        }

        // 3. 转换并保存数据
        List<ExamineeEntity> entities = importList.stream()
                .map(dto -> {
                    ExamineeEntity entity = new ExamineeEntity();

                    entity.setOrgId(importParam.getOrgId());
                    entity.setDepartmentId(importParam.getDepartmentId());
                    entity.setOperatorId(importParam.getOperatorId());

                    BeanUtils.copyProperties(dto, entity);
                    entity.setSceneId(sceneId);
                    return entity;
                })
                .collect(Collectors.toList());

        if (this.saveBatch(entities)) {
            result.setSuccessCount(entities.size());
        }

        return result;
    }

    private ImportResult checkDuplicates(List<ExamineeImportDTO> importList, Long sceneId) {
        ImportResult result = new ImportResult();

        // 检查导入数据内部重复
        Map<String, Long> idCountMap = importList.stream()
                .collect(Collectors.groupingBy(ExamineeImportDTO::getExamineeId, Collectors.counting()));

        List<String> inputDuplicates = idCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!inputDuplicates.isEmpty()) {
            result.setInputDuplicates(inputDuplicates);
            return result;
        }

        // 检查数据库重复
        List<String> examineeIds = importList.stream()
                .map(ExamineeImportDTO::getExamineeId)
                .collect(Collectors.toList());

        List<String> dbDuplicates = this.lambdaQuery()
                .in(ExamineeEntity::getExamineeId, examineeIds)
                .eq(ExamineeEntity::getSceneId, sceneId)
                .list()
                .stream()
                .map(ExamineeEntity::getExamineeId)
                .collect(Collectors.toList());

        if (!dbDuplicates.isEmpty()) {
            result.setDbDuplicates(dbDuplicates);
        }

        return result;
    }
}
