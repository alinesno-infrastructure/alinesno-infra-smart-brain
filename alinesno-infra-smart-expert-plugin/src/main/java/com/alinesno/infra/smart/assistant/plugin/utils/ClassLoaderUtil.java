package com.alinesno.infra.smart.assistant.plugin.utils;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

/**
 * 加载服务类
 */
@Slf4j
public class ClassLoaderUtil {

    /**
     * 方法描述 判断class对象是否带有spring的注解
     * 存放實現
     *
     * @param cla jar中的每一个class
     * @return true 是spring bean   false 不是spring bean
     * @method isSpringBeanClass
     */
    public static boolean isSpringBeanClass(Class<?> cla) {

        if (cla == null) {
            return false;
        }
        //是否是接口
        if (cla.isInterface()) {
            return false;
        }
        //是否是抽象类
        if (Modifier.isAbstract(cla.getModifiers())) {
            return false;
        }
        try {
            if (cla.getAnnotation(Component.class) != null) {
                return true;
            }
        }catch (Exception e){
            log.error("出现异常：{}",e.getMessage());
        }

        try {
            if (cla.getAnnotation(LiteflowComponent.class) != null) {
                return true;
            }
        }catch (Exception e){
            log.error("出现异常：{}",e.getMessage());
        }

        try {
            if (cla.getAnnotation(Repository.class) != null) {
                return true;
            }
        }catch (Exception e){
            log.error("出现异常：{}",e.getMessage());
        }

        try {
            if (cla.getAnnotation(Service.class) != null) {
                return true;
            }
        }catch (Exception e){
            log.error("出现异常：{}",e.getMessage());
        }
        return false;
    }

}
