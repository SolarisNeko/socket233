package com.neko233.socket233.core.env;

/**
 * @author LuoHaoJun on 2023-06-16
 **/
public interface EngineEnvKeys {

    /**
     * 业务对象, 包扫描路径, csv 格式
     * <p>
     * 扫描所有标注了 {@see RouteHandler}
     */
    String BUSINESS_CLASS_SCAN_PATH = "server.business.packagePath";

    String KEY_ENGINE_CONFIG = "networkEngine.configFile";

    /**
     * 编码器 alias name
     */
    String KEY_ENCODER_NAME = "networkEngine.encoder.name";

    /**
     * 解码器 alias name
     */
    String KEY_DECODER_NAME = "networkEngine.decoder.name";

    String KEY_ENGINE_NAME = "networkEngine.protocol.name";
}
