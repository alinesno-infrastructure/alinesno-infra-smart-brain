package com.alinesno.infra.common.web.adapter.base.controller;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.controller.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据字典信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/data/type")
public class SysDictTypeController extends SuperController {

    @Autowired
    private DictTypeService dictTypeService ;

    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{dictId}")
    public AjaxResult getInfo(@PathVariable String dictId)
    {
        return AjaxResult.success("查询成功" , dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds)
    {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return ok();
    }

    /**
     * 刷新字典缓存
     */
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        dictTypeService.resetDictCache();
        return ok();
    }

//    /**
//     * 获取字典选择框列表
//     */
//    @GetMapping("/optionselect")
//    public AjaxResult optionselect()
//    {
//        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
//        return ok(dictTypes);
//    }
}
