package com.alinesno.infra.smart.assistant.gateway.dynamic;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 模拟一个简单的service抽象类，其实也可以是接口，主要是为了把dao带进去，
 * 所以就搞了个抽象类在这里
 * @author rongdi
 * @date 2021-01-06
 */
public abstract class TestService {

    @Autowired
    protected TestDao dao;

    public abstract String sayHello(String msg);

}
