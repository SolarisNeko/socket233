package com.neko233.socket233.core.msgpacket;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.bytes.bytes_impl.OriginalByteArrayData;
import org.jetbrains.annotations.Nullable;

/**
 * 基于 header 头做数据切分.
 *
 * @author LuoHaoJun on 2023-06-13
 **/
public class HeaderLengthMessagePacketHandler implements MessagePacketHandler {

    private final ByteArrayData myBuffer = OriginalByteArrayData.create(1024);

    /**
     * 分包处理
     *
     * @param input 输入的字节
     * @return
     */
    @Nullable
    @Override
    public byte[] handle(ByteArrayData input) {
        // 全部接收
        myBuffer.writeBytesFrom(input);

        // header | body
        // 4 bytes = data size | body (前面数据驱动)
        if (myBuffer.getCanReadSize() >= 4) {
            int dataLength = myBuffer.readInteger(false);
            if (myBuffer.getCanReadSize() < 4 + dataLength) {
                return null;
            }
            myBuffer.addReadIndex(4);

            // 拿到数据
            byte[] bytes = myBuffer.readBytes(dataLength);

            myBuffer.compact();

            return bytes;
        }
        return null;
    }
}
