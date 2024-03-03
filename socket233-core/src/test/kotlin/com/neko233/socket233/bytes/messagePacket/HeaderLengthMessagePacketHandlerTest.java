package com.neko233.socket233.bytes.messagePacket;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.bytes.bytes_impl.NettyByteArrayData;
import com.neko233.socket233.core.msgpacket.HeaderLengthMessagePacketHandler;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author LuoHaoJun on 2023-06-14
 **/
public class HeaderLengthMessagePacketHandlerTest {

    @Test
    public void handle() {
        ByteArrayData b1 = NettyByteArrayData.create(1024);
        String a = "halo, demo";
        b1.writeNumber(a.getBytes(StandardCharsets.UTF_8).length);
        b1.writeString(a);

        HeaderLengthMessagePacketHandler headerLengthMessagePacketHandler = new HeaderLengthMessagePacketHandler();
        byte[] handle = headerLengthMessagePacketHandler.handle(b1);

        String str = new String(handle, StandardCharsets.UTF_8);
        Assert.assertEquals(a, str);
    }

    @Test
    public void handleAll_just_1_message() {
        ByteArrayData b1 = NettyByteArrayData.create(1024);
        String a = "halo, demo";
        b1.writeNumber(a.getBytes(StandardCharsets.UTF_8).length);
        b1.writeString(a);

        HeaderLengthMessagePacketHandler headerLengthMessagePacketHandler = new HeaderLengthMessagePacketHandler();
        List<byte[]> frameList = headerLengthMessagePacketHandler.handleAll(b1);

        Assert.assertEquals(1, frameList.size());

        String str = new String(frameList.get(0), StandardCharsets.UTF_8);
        Assert.assertEquals(a, str);
    }

    @Test
    public void handleAll_10_message() {
        ByteArrayData b1 = NettyByteArrayData.create(1024);

        String a = "halo, demo";
        int forCount = 3;
        for (int i = 0; i < forCount; i++) {
            String newStr = a + "-" + i;
            b1.writeNumber(newStr.getBytes(StandardCharsets.UTF_8).length);
            b1.writeString(newStr);
        }

        HeaderLengthMessagePacketHandler headerLengthMessagePacketHandler = new HeaderLengthMessagePacketHandler();
        List<byte[]> frameList = headerLengthMessagePacketHandler.handleAll(b1);

        Assert.assertEquals(3, frameList.size());

        for (int i = 0; i < forCount; i++) {
            String newStr = a + "-" + i;
            String str = new String(frameList.get(i), StandardCharsets.UTF_8);
            Assert.assertEquals(newStr, str);
        }
    }
}