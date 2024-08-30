package com.alinesno.infra.base.im.gateway.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.im.entity.ChannelEntity;
import com.alinesno.infra.base.im.service.IChannelService;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/infra/base/im/channel")
public class ChannelController extends BaseController<ChannelEntity, IChannelService> {

    @Autowired
    private IChannelService service;

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

    /**
     * createChannel
     * @return
     */
    @PostMapping("/createChannel")
    public AjaxResult createChannel(@RequestBody ChannelEntity entity){

       log.debug("entity = {}" , JSONObject.toJSONString(entity));

        String channelId = service.createChannel(entity) ;

        return AjaxResult.success("操作成功" , channelId) ;
    }

    /**
     * 逻辑删除聊天窗口
     * @return
     */
    @DeleteMapping("/removeChannel")
    public AjaxResult removeChannel(Long channelId){

        service.removeChannel(channelId) ;

        return AjaxResult.success() ;
    }

    /**
     * 获取到用户默认的频道
     * @return
     */
    @GetMapping("/getDefaultChannelId")
    public AjaxResult getDefaultChannelId(){

        Long channelId = service.getDefaultChannelId() ;

        return AjaxResult.success("操作成功." , channelId) ;
    }

    /**
     * 查询出我所有的渠道
     * @return
     */
    @GetMapping("/allMyChannel")
    public AjaxResult allMyChannel(){

        List<ChannelEntity> channelEntities = service.allMyChannel() ;
        return AjaxResult.success(channelEntities) ;
    }

    /**
     * 查看所有的公开频道
     * @return
     */
    @GetMapping("/allPublicChannel")
    public AjaxResult allPublicChannel(){

        List<ChannelEntity> channelEntities = service.allPublicChannel() ;
        return AjaxResult.success(channelEntities) ;
    }

    /**
     * 加入频道
     * @param channelId
     * @return
     */
    @GetMapping("/joinChannel")
    public AjaxResult joinChannel(String channelId){

        long userId = IdUtil.getSnowflakeNextId() ;
        service.jobChannel(userId , channelId) ;

        return AjaxResult.success() ;
    }

    @Override
    public IChannelService getFeign() {
        return this.service;
    }
}

