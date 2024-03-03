package com.neko233.socket233.core.msgpacket;

import com.neko233.socket233.core.bytes.ByteArrayData;
import org.jetbrains.annotations.Nullable;

/**
 * 读取全部, 仅测试用. 不能解决粘包问题
 *
 * @author LuoHaoJun on 2023-06-13
 **/
public class AllMessagePacketHandler implements MessagePacketHandler {

    /**
     * 分包处理
     *
     * @param input 输入的字节
     * @return
     */
    @Nullable
    @Override
    public byte[] handle(ByteArrayData input) {
        return input.readBytes();
    }
}
