package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.api.IndustryRoleCatalogDto;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.TreeSelectDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.mapper.IndustryRoleCatalogMapper;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class IndustryRoleCatalogServiceImpl extends IBaseServiceImpl<IndustryRoleCatalogEntity, IndustryRoleCatalogMapper> implements IIndustryRoleCatalogService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public List<IndustryRoleCatalogEntity> selectCatalogList(IndustryRoleCatalogEntity promptCatalog, PermissionQuery query) {

        LambdaQueryWrapper<IndustryRoleCatalogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(IndustryRoleCatalogEntity.class) ;
        query.toWrapper(queryWrapper);

        List<IndustryRoleCatalogEntity> list = list(queryWrapper) ;

        if(list == null || list.isEmpty()){

            list = new ArrayList<>() ;

            // 默认有一个选项是父类
            IndustryRoleCatalogEntity parent = new IndustryRoleCatalogEntity() ;
            parent.setName("父类对象");
            parent.setId(0L);

            list.add(parent) ;
        }

        return list ;

    }

    @Override
    public void insertCatalog(IndustryRoleCatalogEntity entity) {

        IndustryRoleCatalogEntity info = this.getById(entity.getParentId());
        if(info != null){
            entity.setAncestors(info.getAncestors() + "," + entity.getParentId());
        }

        this.save(entity) ;
    }

    @Override
    public List<TreeSelectDto> selectCatalogTreeList(PermissionQuery query) {

        LambdaQueryWrapper<IndustryRoleCatalogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(IndustryRoleCatalogEntity.class) ;
        query.toWrapper(queryWrapper);

        List<IndustryRoleCatalogEntity> deptTrees = buildDeptTree(list(queryWrapper));
        return deptTrees.stream().map(TreeSelectDto::new).collect(Collectors.toList());
    }

    @Override
    public List<IndustryRoleCatalogDto> allCatalog(PermissionQuery query) {

        LambdaQueryWrapper<IndustryRoleCatalogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(IndustryRoleCatalogEntity.class) ;
        query.toWrapper(queryWrapper);

        List<IndustryRoleCatalogEntity> list = list(queryWrapper) ;
        List<IndustryRoleCatalogDto> listDto = new ArrayList<>() ;

        for(IndustryRoleCatalogEntity e : list){

            IndustryRoleCatalogDto dto = new IndustryRoleCatalogDto() ;
            BeanUtils.copyProperties(e , dto);

            LambdaQueryWrapper<IndustryRoleEntity> wrapper = new LambdaQueryWrapper<>() ;
            wrapper.setEntityClass(IndustryRoleEntity.class);
            query.toWrapper(wrapper);
            wrapper.eq(IndustryRoleEntity::getIndustryCatalog , e.getId()) ;
            List<IndustryRoleEntity> sList = roleService.list(wrapper) ;

            if(!sList.isEmpty()){
                // 将 List<IndustryRoleEntity> 转换为 List<IndustryRoleDto>
                List<IndustryRoleDto> dtoList = sList.stream()
                        .map(IndustryRoleDto::fromEntity) // 使用 fromEntity 方法转换每个 IndustryRoleEntity 到 IndustryRoleDto
                        .toList(); // 收集转换后的结果为 List<IndustryRoleDto>

                dto.setAgents(dtoList);

                listDto.add(dto) ;
            }
        }

        return listDto ;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param prompts 部门列表
     * @return 树结构列表
     */
    public List<IndustryRoleCatalogEntity> buildDeptTree(List<IndustryRoleCatalogEntity> prompts) {

        List<IndustryRoleCatalogEntity> returnList = new ArrayList<>();
        List<Long> tempList = prompts.stream().map(IndustryRoleCatalogEntity::getId).toList();

        for (IndustryRoleCatalogEntity dept : prompts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(prompts, dept);
                returnList.add(dept);
            }
        }

        if (returnList.isEmpty()) {
            returnList = prompts;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<IndustryRoleCatalogEntity> list, IndustryRoleCatalogEntity t) {
        // 得到子节点列表
        List<IndustryRoleCatalogEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (IndustryRoleCatalogEntity tChild : childList) {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<IndustryRoleCatalogEntity> getChildList(List<IndustryRoleCatalogEntity> list, IndustryRoleCatalogEntity t) {
        List<IndustryRoleCatalogEntity> tlist = new ArrayList<>();
        for (IndustryRoleCatalogEntity n : list) {
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<IndustryRoleCatalogEntity> list, IndustryRoleCatalogEntity t) {
        return !getChildList(list, t).isEmpty();
    }

    @Override
    public IndustryRoleCatalogEntity getDefaultCatalog(IndustryRoleCatalogEntity entity) {

        // 添加角色到默认的团队里面
        LambdaQueryWrapper<IndustryRoleCatalogEntity> cateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cateLambdaQueryWrapper.eq(IndustryRoleCatalogEntity::getOrgId, entity.getOrgId());
        cateLambdaQueryWrapper.eq(IndustryRoleCatalogEntity::getFieldProp, "default");

        IndustryRoleCatalogEntity defaultCatalog =  null ;
        long cateCount = count(cateLambdaQueryWrapper);
        if (cateCount == 0) {  // 创建默认团队组织
            defaultCatalog = new IndustryRoleCatalogEntity() ;
            defaultCatalog.setOperatorId(entity.getOperatorId());
            defaultCatalog.setOrgId(entity.getOrgId());
            defaultCatalog.setDepartmentId(entity.getDepartmentId());
            defaultCatalog.setFieldProp("default");

            defaultCatalog.setName("默认团队");
            defaultCatalog.setDescription("默认团队，所有角色都默认添加到该团队里面，您可以通过团队管理进行管理，或者再进行二次分配。");

            save(defaultCatalog);
        }else{
            defaultCatalog = getOne(cateLambdaQueryWrapper);
        }
        return defaultCatalog;
    }
}