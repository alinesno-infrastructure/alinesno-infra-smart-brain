package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamineeGroupService;
import com.alinesno.infra.smart.scene.entity.ExamineeGroupEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图片处理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/examineeGroup")
public class ExamineeGroupController extends BaseController<ExamineeGroupEntity, IExamineeGroupService> {

}
