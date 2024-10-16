package com.alinesno.infra.smart.assistant.role.context;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局上下文变量
 */
public class ContextManager {

    @Getter
    private static ContextManager instance = new ContextManager();

    private final Map<String , ParamBean> contextMap = new HashMap<>();

    private ContextManager(){
    }

    public void put(String key , String value){
        ParamBean paramBean = new ParamBean();
        paramBean.setValue(value);
        paramBean.setAddTime(System.currentTimeMillis());
        contextMap.put(key , paramBean);
    }

    public String get(String key){
        ParamBean paramBean = contextMap.get(key);
        if(paramBean != null){
            return paramBean.getValue();
        }
        return null;
    }

}
