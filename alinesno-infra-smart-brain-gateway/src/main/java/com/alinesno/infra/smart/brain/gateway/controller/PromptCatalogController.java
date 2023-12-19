package com.alinesno.infra.smart.brain.gateway.controller;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.brain.entity.PromptCatalogEntity;
import com.alinesno.infra.smart.brain.service.IPromptCatalogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/infra/smart/brain/promptCatalog")
public class PromptCatalogController extends BaseController<PromptCatalogEntity, IPromptCatalogService> {

    @Autowired
    private IPromptCatalogService service;

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
        TableDataInfo tableDataInfo = this.toPage(model, this.getFeign(), page);

        String jsonData = "[\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":108,\n" +
                "        \"parentId\":0,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"人才管理专家团队\",\n" +
                "        \"orderNum\":1,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":109,\n" +
                "                \"parentId\":108,\n" +
                "                \"ancestors\":\"0,100,102,108\",\n" +
                "                \"deptName\":\"培训专家团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"人才管理专家团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":110,\n" +
                "        \"parentId\":0,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"方案架构师团队\",\n" +
                "        \"orderNum\":2,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":111,\n" +
                "                \"parentId\":110,\n" +
                "                \"ancestors\":\"0,100,102,110\",\n" +
                "                \"deptName\":\"解决方案设计团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"方案架构师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":112,\n" +
                "                \"parentId\":110,\n" +
                "                \"ancestors\":\"0,100,102,110\",\n" +
                "                \"deptName\":\"需求分析团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"方案架构师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":113,\n" +
                "                \"parentId\":110,\n" +
                "                \"ancestors\":\"0,100,102,110\",\n" +
                "                \"deptName\":\"项目管理团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"方案架构师团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":114,\n" +
                "        \"parentId\":102,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"工程师团队\",\n" +
                "        \"orderNum\":3,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":115,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"技术架构团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":116,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"数据库设计团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":117,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"接口测试团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":118,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"技术工程师团队\",\n" +
                "                \"orderNum\":4,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":119,\n" +
                "        \"parentId\":102,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"数据挖掘团队\",\n" +
                "        \"orderNum\":4,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":120,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据规划团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":121,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据采集团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":122,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据挖掘(计算)团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":123,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据服务团队\",\n" +
                "                \"orderNum\":4,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":124,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据分析团队\",\n" +
                "                \"orderNum\":5,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":125,\n" +
                "        \"parentId\":102,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"运维管理团队\",\n" +
                "        \"orderNum\":5,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":126,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"服务器维护团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":127,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"网络维护团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":128,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"数据库维护团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":129,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"安全管理团队\",\n" +
                "                \"orderNum\":4,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]" ;

        tableDataInfo.setRows(JSONArray.parseArray(jsonData));

        return tableDataInfo ;
    }

    @GetMapping("/list")
    public AjaxResult list() {
        String jsonData = "[\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":108,\n" +
                "        \"parentId\":0,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"人才管理专家团队\",\n" +
                "        \"orderNum\":1,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":109,\n" +
                "                \"parentId\":108,\n" +
                "                \"ancestors\":\"0,100,102,108\",\n" +
                "                \"deptName\":\"培训专家团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"人才管理专家团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":110,\n" +
                "        \"parentId\":0,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"方案架构师团队\",\n" +
                "        \"orderNum\":2,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":111,\n" +
                "                \"parentId\":110,\n" +
                "                \"ancestors\":\"0,100,102,110\",\n" +
                "                \"deptName\":\"解决方案设计团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"方案架构师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":112,\n" +
                "                \"parentId\":110,\n" +
                "                \"ancestors\":\"0,100,102,110\",\n" +
                "                \"deptName\":\"需求分析团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"方案架构师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":113,\n" +
                "                \"parentId\":110,\n" +
                "                \"ancestors\":\"0,100,102,110\",\n" +
                "                \"deptName\":\"项目管理团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"方案架构师团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":114,\n" +
                "        \"parentId\":102,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"工程师团队\",\n" +
                "        \"orderNum\":3,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":115,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"技术架构团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":116,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"数据库设计团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":117,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"接口测试团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":118,\n" +
                "                \"parentId\":114,\n" +
                "                \"ancestors\":\"0,100,102,114\",\n" +
                "                \"deptName\":\"技术工程师团队\",\n" +
                "                \"orderNum\":4,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"工程师团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":119,\n" +
                "        \"parentId\":102,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"数据挖掘团队\",\n" +
                "        \"orderNum\":4,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":120,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据规划团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":121,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据采集团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":122,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据挖掘(计算)团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":123,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据服务团队\",\n" +
                "                \"orderNum\":4,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":124,\n" +
                "                \"parentId\":119,\n" +
                "                \"ancestors\":\"0,100,102,119\",\n" +
                "                \"deptName\":\"数据分析团队\",\n" +
                "                \"orderNum\":5,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"数据挖掘团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"createBy\":\"admin\",\n" +
                "        \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "        \"updateBy\":null,\n" +
                "        \"updateTime\":null,\n" +
                "        \"remark\":null,\n" +
                "        \"deptId\":125,\n" +
                "        \"parentId\":102,\n" +
                "        \"ancestors\":\"0,100,102\",\n" +
                "        \"deptName\":\"运维管理团队\",\n" +
                "        \"orderNum\":5,\n" +
                "        \"leader\":\"若依\",\n" +
                "        \"phone\":\"15888888888\",\n" +
                "        \"email\":\"ry@qq.com\",\n" +
                "        \"status\":\"0\",\n" +
                "        \"delFlag\":\"0\",\n" +
                "        \"parentName\":null,\n" +
                "        \"children\":[\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":126,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"服务器维护团队\",\n" +
                "                \"orderNum\":1,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":127,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"网络维护团队\",\n" +
                "                \"orderNum\":2,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":128,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"数据库维护团队\",\n" +
                "                \"orderNum\":3,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"createBy\":\"admin\",\n" +
                "                \"createTime\":\"2023-04-23 16:11:36\",\n" +
                "                \"updateBy\":null,\n" +
                "                \"updateTime\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"deptId\":129,\n" +
                "                \"parentId\":125,\n" +
                "                \"ancestors\":\"0,100,102,125\",\n" +
                "                \"deptName\":\"安全管理团队\",\n" +
                "                \"orderNum\":4,\n" +
                "                \"leader\":\"若依\",\n" +
                "                \"phone\":\"15888888888\",\n" +
                "                \"email\":\"ry@qq.com\",\n" +
                "                \"status\":\"0\",\n" +
                "                \"delFlag\":\"0\",\n" +
                "                \"parentName\":\"运维管理团队\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]" ;
        return AjaxResult.success("" , JSONArray.parseArray(jsonData));
    }

    @Override
    public IPromptCatalogService getFeign() {
        return this.service;
    }
}

