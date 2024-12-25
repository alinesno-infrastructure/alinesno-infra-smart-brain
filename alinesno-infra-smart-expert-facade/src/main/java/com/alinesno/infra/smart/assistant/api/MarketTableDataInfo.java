package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketTableDataInfo extends TableDataInfo {
    private List<?> pushRows ;

    public static MarketTableDataInfo form(TableDataInfo page) {
        MarketTableDataInfo marketTableDataInfo = new MarketTableDataInfo();
        marketTableDataInfo.setCode(page.getCode());
        marketTableDataInfo.setMsg(page.getMsg());
        marketTableDataInfo.setRows(page.getRows());
        marketTableDataInfo.setTotal(page.getTotal());
        return marketTableDataInfo ;
    }
}
