package com.neko233.socket233.core;

import com.neko233.socket233.core.common.Kv;
import com.neko233.socket233.core.engine.LifeCycle;
import com.neko233.socket233.core.engine.NetworkEngineContext;
import com.neko233.socket233.core.engine.request.PacketRequestHandlerApi;
import com.neko233.socket233.core.engine.request.impl.PacketRequestHandlerByDefault;
import com.neko233.socket233.core.env.EngineEnvKeys;
import com.neko233.socket233.core.utils.TimePieceUtils;
import org.slf4j.Logger;

/**
 * 整体
 *
 * @author LuoHaoJun on 2023-05-31
 **/
public interface NetworkEngine extends LifeCycle {

    /**
     * 启动工作流
     *
     * @throws Throwable 抛出异常
     */
    default void startUp() throws Throwable {
        setEngineContextIfNotExist();

        long startUpSpendMs = TimePieceUtils.executeAndReturnSpendMs(() -> {
            try {
                // start lifecycle
                this.init();
                this.preCreate();
                this.create();
                this.postCreate();

            } catch (Throwable t) {
                getLogger().error("[NetworkEngine] 初始化报错", t);
            }
            NetworkEngineContext.instance.register(this);
        });

        getLogger().info("{} start up spend {} ms", getEngineName(), startUpSpendMs);

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.preDestroy();
            this.destroy();
            this.postDestroy();
        }));
    }

    /**
     * 兜底设置, 如果已经有则不生效
     */
    static void setEngineContextIfNotExist() {
        // 处理请求
        NetworkEngineContext.instance.setSingletonIfAbsent(PacketRequestHandlerApi.class, new PacketRequestHandlerByDefault());
    }

    Logger getLogger();

    String getEngineName();

    /**
     * 获取配置文件
     * ps: default resources/ 下
     *
     * @return 配置文件, resourcesPath
     */
    default String getConfigFilePath() {
        return System.getProperty(EngineEnvKeys.KEY_ENGINE_CONFIG, "socket233.properties");
    }

    void checkConfigKv();

    Kv getConfigKv();

    void handleStartUpException(Throwable e);


    default Integer getPort() {
        return getConfigKv()
                .getInt("server.port", 8888);
    }

    /**
     * 业务代码, 所在 package path. 进行包扫描用
     *
     * @return packageName
     */
    default String getBusinessPackagePath() {
        return getConfigKv()
                .getString(EngineEnvKeys.BUSINESS_CLASS_SCAN_PATH);
    }

    /**
     * @return 解码器名字
     */
    default String getDecoderName() {
        return getConfigKv()
                .getString(EngineEnvKeys.KEY_DECODER_NAME,
                        "default");
    }

    /**
     * @return 编码器名字
     */
    default String getEncoderName() {
        return getConfigKv().getString(EngineEnvKeys.KEY_ENCODER_NAME,
                "default");
    }

}
