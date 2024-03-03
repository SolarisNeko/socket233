package com.neko233.socket233.core.utils.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @author LuoHaoJun on 2023-06-15
 **/
public class NettyAttributeUtils {

    public static <T> T get(ChannelHandlerContext context, String attributeName, Class<T> type) {
        AttributeKey<T> key = AttributeKey.valueOf(attributeName);
        // 获取属性
        Attribute<T> attr = context.attr(key);
        return attr.get();
    }

}
