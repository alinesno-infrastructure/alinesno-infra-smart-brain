package com.alinesno.infra.common.facade.pageable;

import com.alinesno.infra.common.facade.wrapper.RpcWrapper;
import com.alinesno.infra.common.facade.wrapper.Wrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页数据
 *
 * @author luoxiaodong
 * @since 2018年8月16日 上午8:59:44
 */
@Slf4j
@Data
public class DatatablesPageBean implements Serializable {

    private int pageSize; // 每页显示
    private int pageNum; // 开始条数

    private int total; // 总条数
    private int code = HttpStatus.OK.value() ; // 状态码
    private Object rows; // 返回的数据

    private int recordsFiltered;
    private Object data;
    private int draw;
    private int recordsTotal;
    private Map<String, Object> condition = new ConcurrentHashMap<>();
    private List<ConditionDto> conditionList = new ArrayList<>();

    public boolean isBootstrapTable() {
        return this.pageSize != 0;
    }

    /**
     * 构建查询参数
     * @param page
     * @param wrapper
     * @return
     * @param <T>
     */
    public <T> RpcWrapper<T> buildWrapper(DatatablesPageBean page, RpcWrapper<T> wrapper) {

        RpcWrapper<T> restWrapper = wrapper == null ? RpcWrapper.build() : wrapper;

        restWrapper.setPageNumber(page.getPageNum());
        restWrapper.setPageSize(page.getPageSize());

        restWrapper.builderCondition(conditionList);
        if (!restWrapper.isHasOrder()) {
            restWrapper.orderBy("add_time", false);
        }

        return restWrapper;
    }

    /**
     * 返回创建条件
     *
     * @return
     */
    public <T> RpcWrapper<T> buildWrapper(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values != null) {
                for (String value : values) {
                    ConditionDto conditionDto = new ConditionDto();

                    conditionDto.setColumn(key);
                    conditionDto.setValue(value);
                    conditionDto.setType(Wrapper.EQ);

//                    conditionList.add(conditionDto);
                }
            }
        }

        RpcWrapper<T> wrapper = RpcWrapper.build();
        return buildWrapper(this, wrapper);
    }

}
