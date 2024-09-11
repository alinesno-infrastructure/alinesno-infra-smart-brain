package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.mapper.IndustryRoleMapper;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
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

//    @Autowired
//    private IRoleChainService roleChainService ;

    @Value("${spring.application.name}")
    private String applicationName;

//    @Autowired
//    private PublishRedisService publishService ;

//    @Resource(name="chainRunner")
//    private IBaseExpertService baseExpert ;

//    @Autowired
//    private INoticeService noticeService ;

    @Override
    public void saveRoleChainInfo(String roleId) {

//        chain.setEnable("1");
//        chain.setChainApplicationName(applicationName);
//        chain.setCreateTime(new Date());
//
//        roleChainService.save(chain) ;
//
//        IndustryRoleEntity role = this.getById(roleId) ;
//        role.setChainId(chain.getId());
//        this.update(role) ;

        // 发送消息用于规则的热更新
//        publishService.sendMsg(MessageConstants.RELOAD_RULE);
    }

    @Override
    public void runRoleChainByRoleId(Map<String , Object> params , long roleId) {

        Assert.notNull(params , "请求参数为空.");

//        IndustryRoleEntity role = getById(roleId) ;
//        RoleChainEntity roleChain = roleChainService.getById(role.getChainId()) ;
//
//        String chainName = roleChain.getChainName() ;
//        Long chainId = roleChain.getId() ;

//        baseExpert.processExpert(params , chainName , chainId);
    }

    @Override
    public void runChainAgent(TaskContentDto dto , long roleId) {

        // TODO 待优化获取多个脚本的问题
        String msg = dto.getCodeContent().get(0).getContent();

//        NoticeEntity noticeEntity = noticeService.getByBusinessId(dto.getBusinessId()) ;
//        NoticeDto noticeDto = new NoticeDto() ;
//
//        if(noticeEntity != null){
//            BeanUtils.copyProperties(noticeEntity , dto);
//            noticeDto.setTaskName(dto.getGenContent());
//        }

        // 发送培训相关的要求到任务中
        Map<String , Object> params = new HashMap<>() ;
        params.put("label1" , msg) ;

        runRoleChainByRoleId(params , roleId);
    }

    @Override
    public List<IndustryRoleEntity> getNewestRole() {

        LambdaQueryWrapper<IndustryRoleEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(IndustryRoleEntity::getAddTime);
        queryWrapper.last("limit 8");

        return this.list(queryWrapper);
    }

    @Override
    public List<IndustryRoleEntity> getRoleByUserName(List<String> users) {

        LambdaQueryWrapper<IndustryRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(IndustryRoleEntity::getRoleName, users);

        if (list(queryWrapper) != null) {
            return list(queryWrapper);
        }

        return null;
    }

    @Override
    public IndustryRoleEntity getDefaultHelpAgent() {

        IndustryRoleEntity role = new IndustryRoleEntity() ;
        role.setId(IdUtil.getSnowflakeNextId());
        role.setRoleName("AIP智能体小助理");
        role.setRoleAvatar("1830185154541305857");

        return role;
    }

}