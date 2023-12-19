package com.alinesno.infra.smart.brain.config;

import com.alinesno.infra.common.facade.enable.EnableActable;
import com.alinesno.infra.common.web.adapter.sso.enable.EnableInfraSsoApi;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableActable
@EnableInfraSsoApi
@ServletComponentScan
@MapperScan("com.alinesno.infra.smart.brain.mapper")
@Configuration
public class AppConfig {
}
