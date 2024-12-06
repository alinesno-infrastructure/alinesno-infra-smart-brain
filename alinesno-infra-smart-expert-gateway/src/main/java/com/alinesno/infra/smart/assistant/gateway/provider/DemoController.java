//package com.alinesno.infra.smart.assistant.gateway.provider;
//
//import com.alinesno.infra.smart.assistant.gateway.dynamic.ApplicationUtil;
//import com.alinesno.infra.smart.assistant.gateway.dynamic.TestService;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * 测试入口类
// * @author rongdi
// * @date 2021-01-06
// */
//@Controller
//public class DemoController implements ApplicationContextAware {
//
//    private static String javaSrc =
//                            """
//                            package com.alinesno.infra.smart.assistant.gateway.dynamic;
//                            public class TestClass extends TestService {
//
//                                @Override
//                                public String sayHello(String msg) {
//                                    return dao.query(msg) ;
//                                }
//                            }
//                            """ ;
//
//    private ApplicationContext applicationContext;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//
//    /**
//     * 测试接口，实际上就是完成动态编译java源码、加载字节码变成Class，装载Class到spring容器，
//     * 获取对象，调用对象的测试
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping("/test")
//    @ResponseBody
//    public String test() throws Exception {
//        /**
//         * 美滋滋的注册源码到spring容器得到一个对象
//         * ApplicationUtil.register(applicationContext, javaSrc);
//         */
//        ApplicationUtil.register(applicationContext,"testClass", javaSrc);
//        /**
//         * 从spring上下文中拿到指定beanName的对象
//         * 也可以 TestService testService = ApplicationUtil.getBean(applicationContext,TestService.class);
//         */
//       TestService testService = ApplicationUtil.getBean(applicationContext,"testClass");
//
//        /**
//         * 直接调用
//         */
//        return testService.sayHello("haha");
//    }
//
//}
