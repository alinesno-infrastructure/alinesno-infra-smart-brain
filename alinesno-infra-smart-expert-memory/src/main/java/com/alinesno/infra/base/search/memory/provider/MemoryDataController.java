package com.alinesno.infra.base.search.memory.provider;

import com.alinesno.infra.base.search.memory.IMemoryBase;
import com.alinesno.infra.base.search.memory.bean.AgentMemoryDto;
import com.alinesno.infra.base.search.memory.bean.MemoryNodeWithScore;
import com.alinesno.infra.base.search.memory.service.IAgentMemoryService;
import com.alinesno.infra.common.facade.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能体记忆库控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/base/search/memoryData")
public class MemoryDataController {

    @Autowired
    private IMemoryBase memoryService ;

    @Autowired
    private IAgentMemoryService agentMemoryService;

    /**
     * 查询记忆库
     */
    @RequestMapping("/query")
    public R<List<MemoryNodeWithScore>> queryMemoryData(@RequestParam String agentId ,
                                                        @RequestParam String targetId ,
                                                        @RequestParam String memoryText) {
        List<MemoryNodeWithScore> memoryData = agentMemoryService.queryMemoryData(agentId , targetId , memoryText) ;
        return R.ok(memoryData , "数据查询成功");
    }

    /**
     * 增加记忆
     * @return
     */
    @PutMapping("/add")
    public R<String> addMemoryData(@RequestBody @Validated AgentMemoryDto dto) {

        log.debug("agentId:{},agentName:{}" , dto.getAgentId(), dto.getAgentName());
        agentMemoryService.addBatchMemoryData(List.of(dto) ,dto);
        return R.ok("数据添加成功");
    }

    /**
     * 批量增加记忆
     * @return
     */
    @PutMapping("/addBatch")
    public R<String> addBatchMemoryData(@RequestBody @Validated List<AgentMemoryDto> dtos ,
                                        @RequestParam String agentId ,
                                        @RequestParam String agentName) {

        log.debug("agentId:{},agentName:{}" , agentId , agentName);

        for(AgentMemoryDto dto : dtos){
            dto.setAgentId(Long.parseLong(agentId));
            dto.setAgentName(agentName);
        }

        AgentMemoryDto dto = new AgentMemoryDto() ;
        dto.setAgentId(Long.parseLong(agentId)) ;
        dto.setAgentName(agentName) ;

        agentMemoryService.addBatchMemoryData(dtos , dto);
        return R.ok("数据添加成功");
    }

    /**
     * 删除记忆库
     * @param memoryId
     * @param data
     * @return
     */
    @DeleteMapping("/delete")
    public R<String> deleteMemoryData(String memoryId, String data) {

        return R.ok("数据删除成功");
    }

}
