package com.neko233.socket233.core.engine.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author LuoHaoJun on 2023-06-15
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMessage {

    // 使用的网络引擎名字, null 使用 default
    @Nullable
    private String networkEngineName;

    // 请求的路径
    private int packetId = 0;

    private byte[] originalByteArray;

    // <自定义key, 解析出来的对象(可以多个)>
    private Map<String, Object> dataMap;

    /**
     * get string by byteArray for 日志
     *
     * @return text
     */
    public String getBodyUtf8StringForLog() {
        return new String(originalByteArray, StandardCharsets.UTF_8);
    }

    public Object getData(String key) {
        if (dataMap == null) {
            return null;
        }
        return dataMap.get(key);
    }

}
