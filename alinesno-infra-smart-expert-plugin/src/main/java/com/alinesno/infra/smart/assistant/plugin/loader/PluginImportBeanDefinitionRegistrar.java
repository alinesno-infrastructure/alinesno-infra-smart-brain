package com.alinesno.infra.smart.assistant.plugin.loader;

import com.alinesno.infra.smart.assistant.plugin.utils.ClassLoaderUtil;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 启动时注册bean核心类
 * 此类用于动态加载jar中的类进行自动注册进系统。
 * @author bask
 * @version 1.0
 * @date 2022/7/5
 * <p>
 */
@Slf4j
public class PluginImportBeanDefinitionRegistrar extends JarLauncher implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    /**
     * 存储Jar文件基础路径
     */
    private String basePath ;

    /**
     * 存储插件地址
     */
    private String central ;

    /**
     * 包前缀，如：com.alinesno.infra.smart.assistant.plugin
     */
    private String packagePrefix ;

    private BeanDefinitionRegistry registry ;

    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        List<PluginLoader.PluginInfo> list = PluginLoader.loadPlugin(central , basePath);
        log.debug("list = {}" , list);

        this.registry = registry ;

        File directory = new File(basePath);
        File[] jarFiles = directory.listFiles((dir, name) -> name.endsWith(".jar"));

        if (jarFiles != null) {
            for (File jarFile : jarFiles) {

                URL url = jarFile.toURI().toURL();
                ClassLoader classLoader = super.createClassLoader(new URL[]{url}) ;

                URL jarUrl = jarFile.toURI().toURL();
                log.debug("jarFile = {}" , jarUrl);

                try (JarFile jf = new JarFile(jarFile)) {
                    Enumeration<JarEntry> entries = jf.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();

                        if (jarEntry.getName().endsWith(".class")) {
                            String className = jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 6);

                            log.debug("--->>>> className = {}" , className);

                            if(className.startsWith(packagePrefix)){
                                Class<?> clazz = null;
                                try {
                                    clazz = classLoader.loadClass(className);
                                    //注册
                                    registerBean(clazz, registry);
                                    log.info("register bean [{}],Class [{}] success.", clazz.getName(), clazz);
                                } catch (Exception e) {
                                    log.error("加载类包异常:{}" , e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 註冊BEAN
     * @param c
     * @param registry
     */
    private void registerBean(Class<?> c, BeanDefinitionRegistry registry) {
        String className = c.getName();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(c);
        BeanDefinition beanDefinition = builder.getBeanDefinition();

        // 配置注册bean的名称
        if(c.getAnnotation(LiteflowComponent.class) != null){
            LiteflowComponent liteflowComponent = c.getAnnotation(LiteflowComponent.class) ;
            className = liteflowComponent.value() ;
        }

        if (ClassLoaderUtil.isSpringBeanClass(c)) {
            registry.registerBeanDefinition(className, beanDefinition);
        }
    }


    /**
     * 因加载顺序原因，则获取配置不用通过@Value来获取。
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.basePath = environment.getProperty("alinesno.infra.smart.assistant.plugin.path");
        this.central = environment.getProperty("alinesno.infra.smart.assistant.plugin.central");
        this.packagePrefix = environment.getProperty("alinesno.infra.smart.assistant.plugin.prefix" , "com.alinesno.infra.smart.assistant.plugin");
    }

    /**
     * 加载单个插件
     * @param pluginLocalPath
     */
    @SneakyThrows
    public void loadPlugin(String pluginLocalPath) {

        File jarFile = new File(pluginLocalPath) ;

        URL url = jarFile.toURI().toURL();
        ClassLoader classLoader = super.createClassLoader(new URL[]{url}) ;

        URL jarUrl = jarFile.toURI().toURL();
        log.debug("jarFile = {}" , jarUrl);

        try (JarFile jf = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();

                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 6);

                    log.debug("--->>>> className = {}" , className);

                    if(className.startsWith(packagePrefix)){
                        Class<?> clazz;
                        try {
                            clazz = classLoader.loadClass(className);

                            //注册
                            registerBean(clazz, registry);

                            log.info("register bean [{}],Class [{}] success.", clazz.getName(), clazz);
                        } catch (Exception e) {
                            log.error("加载类包异常:{}" , e.getMessage());
                        }
                    }
                }
            }
        }
    }

}