package com.alinesno.infra.smart.assistant.template.controller;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.template.entity.RoleTemplateEntity;
import com.alinesno.infra.smart.assistant.template.service.IRoleTemplateService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目模块 前端控制器
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Api(tags = "插件模块")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/roleTemplate")
public class RoleTemplateController extends BaseController<RoleTemplateEntity, IRoleTemplateService> {

    @Autowired
    private IRoleTemplateService pluginService;

    /**
     * 获取项目模块的DataTables数据
     *
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, getFeign(), page);
    }

    /**
     * 获取过滤模板分类
     * @return
     */
    @PostMapping("/getFilterTemplate")
    public AjaxResult getFilterTemplate() {

        String _SCREEN = "initializr.admin.project.template.screen" ;

        // 初始化 screen 列表并添加示例数据
        List<FilterTemplateBean.ItemName> screen = new ArrayList<>();
        screen.add(new FilterTemplateBean.ItemName("screen_code_1", "团队管理"));
        screen.add(new FilterTemplateBean.ItemName("screen_code_2", "产品运维"));
        screen.add(new FilterTemplateBean.ItemName("screen_code_3", "市场运营"));
        screen.add(new FilterTemplateBean.ItemName("screen_code_3", "销售营销"));
        screen.add(new FilterTemplateBean.ItemName("screen_code_4", "解决文案"));
        screen.add(new FilterTemplateBean.ItemName("screen_code_5", "产品客服"));
        screen.add(new FilterTemplateBean.ItemName("screen_code_6", "其它"));

        List<FilterTemplateBean> l = new ArrayList<FilterTemplateBean>() ;

        FilterTemplateBean b1 = new FilterTemplateBean() ;
        b1.setCodeValue(_SCREEN) ;
        b1.setName("场景") ;
        b1.setItems(screen) ;

        l.add(b1) ;

        return AjaxResult.success(l) ;
    }

    /**
     * 同步集成模板
     * @param gitUrl 仓库地址
     * @return
     * @throws Exception
     */
    @GetMapping("/syncTemplates")
    public AjaxResult syncTemplates(String gitUrl) throws Exception {

//        if(StringUtil.isEmpty(gitUrl)) {
//            String _SYNC_TEMPLATE_GIT_URL = "alinesno.admin.init.template.git.url" ;
//            ManagerSettingsEntity managerSettings = authorityConfigClient.getConfigByKey(_SYNC_TEMPLATE_GIT_URL) ;
//
//            gitUrl = managerSettings.getConfigValue() ;
//        }

        gitUrl = "https://gitee.com/alinesno-cloud/alinesno-infra-plugins-templates.git" ;
        Assert.notNull(gitUrl , "仓库地址为空.");

        log.debug("gitUrl = {}" , gitUrl) ;

        pluginService.syncRoleTemplate(1L , gitUrl) ;

        return AjaxResult.success() ;

    }

    @Data
    static
    class FilterTemplateBean {
        private String name ;
        private String codeValue ;
        private List<ItemName> items ;

        @Data
        @AllArgsConstructor
        static
        class ItemName {
            private String code ;
            private String name ;
        }
    }

    @Override
    public IRoleTemplateService getFeign() {
        return pluginService;
    }
}
