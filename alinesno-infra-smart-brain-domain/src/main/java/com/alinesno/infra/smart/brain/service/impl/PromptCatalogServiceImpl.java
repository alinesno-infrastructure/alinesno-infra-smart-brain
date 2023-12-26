package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.brain.api.TreeSelectDto;
import com.alinesno.infra.smart.brain.entity.PromptCatalogEntity;
import com.alinesno.infra.smart.brain.mapper.PromptCatalogMapper;
import com.alinesno.infra.smart.brain.service.IPromptCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Prompt指令类型Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class PromptCatalogServiceImpl extends IBaseServiceImpl<PromptCatalogEntity, PromptCatalogMapper> implements IPromptCatalogService {

    @Override
    public List<PromptCatalogEntity> selectCatalogList(PromptCatalogEntity promptCatalog) {

        List<PromptCatalogEntity> list = list() ;

        if(list == null || list.isEmpty()){

            list = new ArrayList<>() ;

            // 默认有一个选项是父类
            PromptCatalogEntity parent = new PromptCatalogEntity() ;
            parent.setName("父类对象");
            parent.setId(0L);

            list.add(parent) ;
        }

        return list ;

    }

    @Override
    public void insertCatalog(PromptCatalogEntity entity) {

        PromptCatalogEntity info = this.getById(entity.getParentId());
        if(info != null){
            entity.setAncestors(info.getAncestors() + "," + entity.getParentId());
        }

        this.save(entity) ;
    }

    @Override
    public List<TreeSelectDto> selectCatalogTreeList() {

        List<PromptCatalogEntity> deptTrees = buildDeptTree(list());
        return deptTrees.stream().map(TreeSelectDto::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param prompts 部门列表
     * @return 树结构列表
     */
    public List<PromptCatalogEntity> buildDeptTree(List<PromptCatalogEntity> prompts) {

        List<PromptCatalogEntity> returnList = new ArrayList<>();
        List<Long> tempList = prompts.stream().map(PromptCatalogEntity::getId).toList();

        for (PromptCatalogEntity dept : prompts) {
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
    private void recursionFn(List<PromptCatalogEntity> list, PromptCatalogEntity t) {
        // 得到子节点列表
        List<PromptCatalogEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (PromptCatalogEntity tChild : childList) {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<PromptCatalogEntity> getChildList(List<PromptCatalogEntity> list, PromptCatalogEntity t) {
        List<PromptCatalogEntity> tlist = new ArrayList<>();
        for (PromptCatalogEntity n : list) {
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<PromptCatalogEntity> list, PromptCatalogEntity t) {
        return !getChildList(list, t).isEmpty();
    }
}
