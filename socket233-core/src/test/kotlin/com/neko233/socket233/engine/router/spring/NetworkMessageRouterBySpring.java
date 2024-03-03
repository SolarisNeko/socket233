//package com.neko233.networkengine.engine.router.spring;
//
//import com.neko233.networkengine.core.engine.NetworkEngineContext;
//import com.neko233.networkengine.core.engine.annotation.RouteHandler;
//import com.neko233.networkengine.core.engine.router.NetworkMessageRouter;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ClassUtils;
//
//import java.util.Collection;
//
//@Component
//public class NetworkMessageRouterBySpring extends NetworkMessageRouter implements ApplicationContextAware {
//
//    private static ApplicationContext appContext = null;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        appContext = applicationContext;
//
//        // 设置如何 Router 适配 Spring 部分
//        NetworkEngineContext.Instance.setSingleton(NetworkMessageRouter.class, this);
//    }
//
//
//    @Override
//    public Collection<Object> getAllRouterHandlerObjectList(String packageName) {
//        Collection<Object> values = appContext.getBeansWithAnnotation(RouteHandler.class).values();
//        return values;
//    }
//
//
//    @NotNull
//    @Override
//    public Class<?> getClassFromObj(Object maybeProxyObj) {
//        return ClassUtils.getUserClass(maybeProxyObj);
//    }
//}
