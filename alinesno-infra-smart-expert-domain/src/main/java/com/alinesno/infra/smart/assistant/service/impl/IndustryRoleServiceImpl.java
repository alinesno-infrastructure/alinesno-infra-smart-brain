package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.NoticeEntity;
import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;
import com.alinesno.infra.smart.assistant.mapper.IndustryRoleMapper;
import com.alinesno.infra.smart.assistant.redis.MessageConstants;
import com.alinesno.infra.smart.assistant.redis.PublishRedisService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.INoticeService;
import com.alinesno.infra.smart.assistant.service.IRoleChainService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class IndustryRoleServiceImpl extends IBaseServiceImpl<IndustryRoleEntity, IndustryRoleMapper> implements IIndustryRoleService {

    @Autowired
    private IRoleChainService roleChainService ;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private PublishRedisService publishService ;

    @Resource(name="chainRunner")
    private IBaseExpertService baseExpert ;

    @Autowired
    private INoticeService noticeService ;

    @Override
    public void saveRoleChainInfo(RoleChainEntity chain , String roleId) {

        chain.setEnable("1");
        chain.setChainApplicationName(applicationName);
        chain.setCreateTime(new Date());

        roleChainService.save(chain) ;

        IndustryRoleEntity role = this.getById(roleId) ;
        role.setChainId(chain.getId());
        this.update(role) ;

        // 发送消息用于规则的热更新
        publishService.sendMsg(MessageConstants.RELOAD_RULE);
    }

    @Override
    public void runRoleChainByRoleId(Map<String , Object> params , long roleId , NoticeDto noticeDto) {

        Assert.notNull(params , "请求参数为空.");

        IndustryRoleEntity role = getById(roleId) ;
        RoleChainEntity roleChain = roleChainService.getById(role.getChainId()) ;

        String chainName = roleChain.getChainName() ;
        Long chainId = roleChain.getId() ;

        baseExpert.processExpert(params , chainName , chainId , noticeDto);
    }

    @Override
    public void runChainAgent(TaskContentDto dto , long roleId) {

        // TODO 待优化获取多个脚本的问题
        String msg = dto.getCodeContent().get(0).getContent();

        NoticeEntity noticeEntity = noticeService.getByBusinessId(dto.getBusinessId()) ;
        NoticeDto noticeDto = new NoticeDto() ;

        if(noticeEntity != null){
            BeanUtils.copyProperties(noticeEntity , dto);
            noticeDto.setTaskName(dto.getGenContent());
        }

        // 发送培训相关的要求到任务中
        Map<String , Object> params = new HashMap<>() ;
        params.put("label1" , msg) ;

        runRoleChainByRoleId(params , roleId , noticeDto);
    }

}