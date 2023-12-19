package com.alinesno.infra.smart.brain.gateway.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
import com.alinesno.infra.smart.brain.service.IPromptPostsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/brain/promptPosts")
public class PromptPostsController extends BaseController<PromptPostsEntity, IPromptPostsService> {

    @Autowired
    private IPromptPostsService service;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    @Override
    public AjaxResult save(Model model, @RequestBody PromptPostsEntity entity) throws Exception {
        service.savePromptPost(entity) ;
        return this.ok();
    }

    /**
     *
     * @return
     */
    @SneakyThrows
    @GetMapping("/getPromptContent")
    public AjaxResult getPromptContent(String postId){
        PromptPostsEntity e = service.getById(postId)  ;
        List<PromptMessageDto> prompts = JSONArray.parseArray(e.getPromptContent() , PromptMessageDto.class) ;

        return AjaxResult.success(prompts) ;
    }

    /**
     *
     * @return
     */
    @PostMapping("/updatePromptContent")
    public AjaxResult updatePromptContent(@RequestBody List<PromptMessageDto> messageDto , String postId){
        service.updatePromptContent(messageDto , postId) ;
        return ok() ;
    }

    @GetMapping("/catalogTreeSelect")
    public AjaxResult catalogTreeSelect(){
        String dataJson = "[\n" +
                "    {\n" +
                "        \"id\":1,\n" +
                "        \"parentId\":0,\n" +
                "        \"label\":\"01_人才管理专家团队\",\n" +
                "        \"weight\":0,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"id\":4,\n" +
                "                \"parentId\":1,\n" +
                "                \"label\":\"04_培训专家团队\",\n" +
                "                \"weight\":0\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":2,\n" +
                "        \"parentId\":0,\n" +
                "        \"label\":\"02_方案架构师团队\",\n" +
                "        \"weight\":0,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"id\":5,\n" +
                "                \"parentId\":2,\n" +
                "                \"label\":\"05_项目管理团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":1,\n" +
                "                \"parentId\":2,\n" +
                "                \"label\":\"01_解决方案设计团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":2,\n" +
                "                \"parentId\":2,\n" +
                "                \"label\":\"02_需求分析团队\",\n" +
                "                \"weight\":0\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":3,\n" +
                "        \"parentId\":0,\n" +
                "        \"label\":\"03_工程师团队\",\n" +
                "        \"weight\":0,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"id\":6,\n" +
                "                \"parentId\":3,\n" +
                "                \"label\":\"06_数据库设计团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":7,\n" +
                "                \"parentId\":3,\n" +
                "                \"label\":\"07_接口测试团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":8,\n" +
                "                \"parentId\":3,\n" +
                "                \"label\":\"08_技术工程师团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":3,\n" +
                "                \"parentId\":3,\n" +
                "                \"label\":\"03_技术架构团队\",\n" +
                "                \"weight\":0\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":4,\n" +
                "        \"parentId\":0,\n" +
                "        \"label\":\"04_数据挖掘团队\",\n" +
                "        \"weight\":0,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"id\":13,\n" +
                "                \"parentId\":4,\n" +
                "                \"label\":\"13_数据服务团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":14,\n" +
                "                \"parentId\":4,\n" +
                "                \"label\":\"14_数据分析团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":10,\n" +
                "                \"parentId\":4,\n" +
                "                \"label\":\"10_数据规划团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":12,\n" +
                "                \"parentId\":4,\n" +
                "                \"label\":\"12_数据挖掘(计算)团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":11,\n" +
                "                \"parentId\":4,\n" +
                "                \"label\":\"11_数据采集团队\",\n" +
                "                \"weight\":0\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":5,\n" +
                "        \"parentId\":0,\n" +
                "        \"label\":\"05_运维管理团队\",\n" +
                "        \"weight\":0,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"id\":9,\n" +
                "                \"parentId\":5,\n" +
                "                \"label\":\"09_运维管理团队\",\n" +
                "                \"weight\":0\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":6,\n" +
                "        \"parentId\":0,\n" +
                "        \"label\":\"06_业务运营团队\",\n" +
                "        \"weight\":0,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"id\":15,\n" +
                "                \"parentId\":6,\n" +
                "                \"label\":\"15_产品市场团队\",\n" +
                "                \"weight\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":16,\n" +
                "                \"parentId\":6,\n" +
                "                \"label\":\"16_产品客服团队\",\n" +
                "                \"weight\":0\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]" ;
        return AjaxResult.success("success" , JSONArray.parse(dataJson)) ;
    }

    @Override
    public IPromptPostsService getFeign() {
        return this.service;
    }
}

