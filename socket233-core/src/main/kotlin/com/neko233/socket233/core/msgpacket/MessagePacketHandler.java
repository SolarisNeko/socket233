package com.neko233.socket233.core.msgpacket;

import com.neko233.socket233.core.bytes.ByteArrayData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息包处理器 | 将 byteArrayData --> byte[] 基于自定义的分包粘包处理
 *
 * @author LuoHaoJun on 2023-06-13
 **/
public interface MessagePacketHandler {

    /**
     * 处理单个包
     *
     * @param byteArrayData 字节数据
     * @return 单个消息包 byte[]{header + body} | 如果不足以单个包体, 则 return null
     */
    @Nullable
    byte[] handle(ByteArrayData byteArrayData);

    /**
     * 尽可能的分包粘包处理所有
     *
     * @param byteArrayData 字节数据
     * @return byte[] list
     */
    @Nullable
    default List<byte[]> handleAll(ByteArrayData byteArrayData) {
        List<byte[]> bytes = new ArrayList<>(1);
        while (true) {
            byte[] frame = this.handle(byteArrayData);
            if (frame == null) {
                break;
            }
            bytes.add(frame);
        }
        return bytes;
    }

}
