//package com.neko233.networkengine.engine.router;
//
//import com.neko233.networkengine.core.engine.NetworkEngineContext;
//import com.neko233.networkengine.core.engine.annotation.function.InjectData;
//import com.neko233.networkengine.core.engine.annotation.RequestMethod;
//import com.neko233.networkengine.core.engine.annotation.RouteHandler;
//import com.neko233.networkengine.core.engine.router.NetworkMessageRouter;
//import com.neko233.networkengine.core.engine.router.request.RequestMessageContext;
//import com.neko233.networkengine.core.env.EngineEnv;
//import com.neko233.networkengine.core.env.EngineEnvKeys;
//import com.neko233.skilltree.commons.core.utils.MapUtils233;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//
//import java.nio.charset.StandardCharsets;
//
///**
// * @author LuoHaoJun on 2023-06-16
// **/
//@Slf4j
//@RouteHandler
//@SpringBootApplication
//public class NetworkMessageRouterTest {
//
//
//    @RequestMethod("demo")
//    public void demo(@InjectData Object data) {
//        log.info("path = demo. data = {}", data);
//    }
//
//    @RequestMethod("no-args")
//    public void noArgsMethod() {
//        log.info("path = no-args. data = {}", "no args!");
//    }
//
//    @Test
//    void route() {
//        EngineEnv.set(EngineEnvKeys.KEY_SCAN_PACKAGE_PATH, NetworkMessageRouter.class.getPackage().getName());
//
//        SpringApplication.run(NetworkMessageRouterTest.class);
//
//        RequestMessageContext build = RequestMessageContext.builder()
//                .routePath("demo")
//                .originalByteArray(String.valueOf(1).getBytes(StandardCharsets.UTF_8))
//                .dataMap(MapUtils233.of(
//                        "data", 1
//                ))
//                .build();
//
//
//        NetworkMessageRouter router = NetworkEngineContext.Instance.getSingletonNotNull(NetworkMessageRouter.class);
//        router.route(build);
//        router.route(build);
//    }
//
//}