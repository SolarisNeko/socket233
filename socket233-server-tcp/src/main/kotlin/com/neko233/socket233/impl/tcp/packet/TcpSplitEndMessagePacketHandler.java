package com.neko233.socket233.impl.tcp.packet;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.bytes.bytes_impl.OriginalByteArrayData;
import com.neko233.socket233.core.msgpacket.MessagePacketHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * TCP 粘包分包问题. 通过 ";;" 进行切割包
 *
 * @author LuoHaoJun on 2023-06-13
 **/
public class TcpSplitEndMessagePacketHandler implements MessagePacketHandler {

    private final ByteArrayData buffer = OriginalByteArrayData.create(8192);

    /**
     * 分包处理
     *
     * @param input 输入的字节
     * @return
     */
    @Nullable
    @Override
    public byte[] handle(ByteArrayData input) {
        // 缓存所有输入, 并整理 input
        byte[] bytes = input.readBytes();
        buffer.writeBytes(bytes);
        input.compact();

        // split
        byte[] splitBytes = ";;".getBytes(StandardCharsets.UTF_8);

        // 切割包
        return buffer.splitSingleEndByBytes(splitBytes);
    }
}
