package com.alinesno.infra.base.search.service.impl;

import com.alinesno.infra.base.search.api.SearchConfigDto;
import com.alinesno.infra.base.search.entity.SearchConfigEntity;
import com.alinesno.infra.base.search.mapper.SearchConfigMapper;
import com.alinesno.infra.base.search.service.ISearchConfigService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author LuoXiaoDong
 * @version 1.0.0
 */
@Slf4j
@Service
public class SearchConfigServiceImpl extends IBaseServiceImpl<SearchConfigEntity, SearchConfigMapper> implements ISearchConfigService {

    @Override
    public void saveConfiguration(SearchConfigDto searchConfigDTO) {

        // 查询出当前组织是否已经有配置，没有的话，则保存，有的话，则更新
        LambdaQueryWrapper<SearchConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SearchConfigEntity::getOrgId, searchConfigDTO.getOrgId()) ;
        SearchConfigEntity searchConfigEntity = getOne(queryWrapper) ;

        if(searchConfigEntity == null){
            searchConfigEntity = new SearchConfigEntity() ;
            BeanUtils.copyProperties(searchConfigDTO, searchConfigEntity);
            save(searchConfigEntity);
        }else{
            BeanUtils.copyProperties(searchConfigDTO, searchConfigEntity);
            updateById(searchConfigEntity);
        }

    }

    @Override
    public SearchConfigDto findConfiguration(Long orgId) {

        SearchConfigEntity searchConfigEntity = getOne(new LambdaQueryWrapper<SearchConfigEntity>().eq(SearchConfigEntity::getOrgId, orgId)) ;
        if(searchConfigEntity != null){
            SearchConfigDto searchConfigDTO = new SearchConfigDto() ;
            BeanUtils.copyProperties(searchConfigEntity, searchConfigDTO);
            return searchConfigDTO ;
        }

        return null;

    }
}
