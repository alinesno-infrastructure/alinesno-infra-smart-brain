package com.alinesno.infra.base.search.service.impl;

import com.alinesno.infra.base.search.api.TreeSelectDto;
import com.alinesno.infra.base.search.entity.AssetCatalogEntity;
import com.alinesno.infra.base.search.mapper.AssetCatalogMapper;
import com.alinesno.infra.base.search.service.IAssetCatalogService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产编目Service业务层处理
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Slf4j
@Service
public class AssetCatalogServiceImpl extends IBaseServiceImpl<AssetCatalogEntity, AssetCatalogMapper> implements IAssetCatalogService {

//    @Autowired
//    private IManifestService manifestService;

    @Override
    public List<AssetCatalogEntity> selectCatalogList(AssetCatalogEntity promptCatalog, PermissionQuery query) {

        LambdaQueryWrapper<AssetCatalogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(AssetCatalogEntity.class) ;
        query.toWrapper(queryWrapper);

        List<AssetCatalogEntity> list = list(queryWrapper);

        if (list == null || list.isEmpty()) {
            list = new ArrayList<>();
        }

        // 默认有一个选项是父类
        AssetCatalogEntity vectorCatalogParent = new AssetCatalogEntity();
        vectorCatalogParent.setName("向量库检索目录");
        vectorCatalogParent.setId(1001L);
        list.add(vectorCatalogParent);

//        AssetCatalogEntity documentCatalogParent = new AssetCatalogEntity();
//        documentCatalogParent.setName("全文检索目录");
//        documentCatalogParent.setId(1002L);
//        list.add(documentCatalogParent);

        return list;

    }

    @Override
    public void insertCatalog(AssetCatalogEntity entity) {

        AssetCatalogEntity info = this.getById(entity.getParentId());
        if (info != null) {
            entity.setAncestors(info.getAncestors() + "," + entity.getParentId());
        }

        this.save(entity);
    }

    @Override
    public List<TreeSelectDto> selectCatalogTreeList(PermissionQuery query, String type) {

        long parentId = -1 ;
        if (StringUtils.isNotEmpty(type)) {
            if ("dataset".equals(type)) {
                parentId = 1001L ;
            } else if ("document".equals(type)) {
                parentId = 1002L ;
            }
        }

        LambdaQueryWrapper<AssetCatalogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(AssetCatalogEntity.class) ;
        query.toWrapper(queryWrapper);

        List<AssetCatalogEntity> deptTrees = buildDeptTree(list(queryWrapper) , parentId);
        return deptTrees.stream().map(TreeSelectDto::new).collect(Collectors.toList());
    }

    @Override
    public List<AssetCatalogEntity> topCatalog(int count, PermissionQuery query) {

        LambdaQueryWrapper<AssetCatalogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(AssetCatalogEntity.class) ;
        query.toWrapper(queryWrapper);
        queryWrapper.orderByDesc(AssetCatalogEntity::getOrderNum);
        queryWrapper.last("limit " + count);

        return list(queryWrapper);
    }

    @Override
    public List<TreeSelectDto> catalogManifestTreeSelect(PermissionQuery query , String type) {
        return selectCatalogTreeList(query , type);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param list 部门列表
     * @return 树结构列表
     */
    public List<AssetCatalogEntity> buildDeptTree(List<AssetCatalogEntity> list, long parentId) {

//        List<AssetCatalogEntity> returnList = new ArrayList<>();
//        List<Long> tempList = list.stream().map(AssetCatalogEntity::getId).toList();
//
//        for (AssetCatalogEntity dept : list) {
//            // 如果是顶级节点, 遍历该父节点的所有子节点
//            if (!tempList.contains(dept.getParentId())) {
//                recursionFn(list, dept);
//                returnList.add(dept);
//            }
//        }
//
//        if (returnList.isEmpty()) {
//            returnList = list;
//        }
//        return returnList;

        List<AssetCatalogEntity> returnList = new ArrayList<>();
        if (parentId == -1) { // 这里假设传入 -1 时走原来的逻辑，可根据实际情况修改这个判断条件
            List<Long> tempList = list.stream().map(AssetCatalogEntity::getId).toList();

            for (AssetCatalogEntity dept : list) {
                // 如果是顶级节点, 遍历该父节点的所有子节点
                if (!tempList.contains(dept.getParentId())) {
                    recursionFn(list, dept);
                    returnList.add(dept);
                }
            }

            if (returnList.isEmpty()) {
                returnList = list;
            }
        } else {
            // 当传入有效 parentId 时，只遍历该父节点下的所有子节点
            for (AssetCatalogEntity dept : list) {
                if (dept.getParentId() != null && dept.getParentId() == parentId) {
                    recursionFn(list, dept);
                    returnList.add(dept);
                }
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<AssetCatalogEntity> list, AssetCatalogEntity t) {
        // 得到子节点列表
        List<AssetCatalogEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (AssetCatalogEntity tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<AssetCatalogEntity> getChildList(List<AssetCatalogEntity> list, AssetCatalogEntity t) {
        List<AssetCatalogEntity> tlist = new ArrayList<>();
        for (AssetCatalogEntity n : list) {
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<AssetCatalogEntity> list, AssetCatalogEntity t) {
        return !getChildList(list, t).isEmpty();
    }
}
