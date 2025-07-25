package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.provider;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供模板分组的接口能力
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterLayout/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterLayoutProvider {
}
