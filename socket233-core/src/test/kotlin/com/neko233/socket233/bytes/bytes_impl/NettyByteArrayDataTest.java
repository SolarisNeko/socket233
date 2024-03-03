package com.neko233.socket233.bytes.bytes_impl;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.bytes.bytes_impl.NettyByteArrayData;
import com.neko233.socket233.core.bytes.exception.BytesException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author LuoHaoJun on 2023-06-13
 **/
public class NettyByteArrayDataTest {

    @Test
    public void create() {
        ByteArrayData byteArrayData = NettyByteArrayData.create(5);
        Assert.assertNotNull(byteArrayData);
    }

    @Test
    public void createNewEmpty() {
        ByteArrayData byteArrayData = NettyByteArrayData.create(5);
        ByteArrayData newEmpty = byteArrayData.createNewEmpty(10);

        String s = newEmpty.writeString("abc")
                .readString();
        Assert.assertEquals("abc", s);
    }

    @Test
    public void createCopy() {
        ByteArrayData newEmpty = NettyByteArrayData.create(10);

        ByteArrayData byteArrayData = newEmpty.writeString("abc");
        ByteArrayData copy = byteArrayData.createCopy();

        String s1 = byteArrayData.readString();
        String s2 = copy.readString();

        Assert.assertEquals("abc", s1);
        Assert.assertEquals("abc", s2);
    }

    @Test
    public void writeBytes() {

    }

    @Test
    public void splitByString() {

        ByteArrayData newEmpty = NettyByteArrayData.create(10);
        ByteArrayData byteArrayData = newEmpty.writeString("a1|a2|a3|");
        List<String> list = byteArrayData.splitEndByString("|");
        Assert.assertEquals("a1|a2|a3", String.join("|", list));
    }

    @Test
    public void splitByString2() {

        ByteArrayData newEmpty = NettyByteArrayData.create(10);
        ByteArrayData byteArrayData = newEmpty.writeString("a1|a2|a3");
        List<String> list = byteArrayData.splitEndByString("|");

        Assert.assertEquals("a1|a2", String.join("|", list));
    }


    @Test
    public void readBytes() {
    }

    @Test
    public void testReadBytes() {
    }

    @Test
    public void getReadIndex() {
        ByteArrayData newEmpty = NettyByteArrayData.create(10);

        ByteArrayData byteArrayData = newEmpty.writeString("abc");

        int canReadSize1 = byteArrayData.getCanReadSize();
        // 读取, 同时会修改 readIndex
        String s1 = byteArrayData.readString();
        int canReadSize2 = byteArrayData.getCanReadSize();

        Assert.assertEquals(3, canReadSize1);
        Assert.assertEquals(0, canReadSize2);
    }

    @Test
    public void getWriteIndex() {
        ByteArrayData newEmpty = NettyByteArrayData.create(10);

        ByteArrayData byteArrayData = newEmpty.writeString("abc");

        Assert.assertEquals(3, byteArrayData.getWriteIndex());
    }

    @Test
    public void setReadWriteIndex() {
        ByteArrayData newEmpty = NettyByteArrayData.create(10);

        ByteArrayData byteArrayData = newEmpty.writeString("abc");

        int canReadSize1 = byteArrayData.getCanReadSize();
        // 读取, 同时会修改 readIndex
        String s1 = byteArrayData.readString();
        int canReadSize2 = byteArrayData.getCanReadSize();

        Assert.assertEquals(3, canReadSize1);
        Assert.assertEquals(0, canReadSize2);

        byteArrayData.setReadWriteIndex(0, 4);

        Assert.assertEquals(0, byteArrayData.getReadIndex());
        Assert.assertEquals(4, byteArrayData.getWriteIndex());

        // 最大容量
        ByteArrayData newMaxBytes = NettyByteArrayData.create(10, 10);
        // 超出, 抛异常
        Assert.assertThrows(BytesException.class, () -> {
            newMaxBytes.setReadWriteIndex(1, 100);
        });


    }

    @Test
    public void readTo() {
        ByteArrayData newEmpty = NettyByteArrayData.create(10);

        ByteArrayData byteArrayData = newEmpty.writeString("abc");

        String a = byteArrayData.readToString(1);

        Assert.assertEquals("a", a);
        Assert.assertEquals(1, byteArrayData.getReadIndex());
    }

    @Test
    public void readFrom() {
        ByteArrayData newEmpty = NettyByteArrayData.create(10);

        ByteArrayData byteArrayData = newEmpty.writeString("abc");

        String a = byteArrayData.readStringFrom(1);

        Assert.assertEquals("a", a);
        Assert.assertEquals(0, byteArrayData.getReadIndex());
    }

    @Test
    public void compact() {
        ByteArrayData newEmpty = NettyByteArrayData.create(10);

        ByteArrayData byteArrayData = newEmpty.writeString("abc");

        String a = byteArrayData.readToString(1);

        Assert.assertEquals("a", a);
        Assert.assertEquals(1, byteArrayData.getReadIndex());

        byteArrayData.compact();
        Assert.assertEquals(0, byteArrayData.getReadIndex());
    }


    @Test
    public void writeNumberAndReadNumber() {
        ByteArrayData bytes = NettyByteArrayData.create(10);

        // short
        Byte b = 1;
        bytes.writeByte(b);
        Assert.assertEquals(b, bytes.readByte());
        Assert.assertEquals(Byte.BYTES, bytes.getReadIndex());
        bytes.compact();

        // short
        Short s = 1;
        bytes.writeNumber(s);
        Assert.assertEquals(s, bytes.readShort());
        Assert.assertEquals(Short.BYTES, bytes.getReadIndex());
        bytes.compact();

        // integer
        bytes.writeNumber(1);
        int integer = bytes.readInteger();
        Assert.assertEquals(1, integer);
        Assert.assertEquals(Integer.BYTES, bytes.getReadIndex());
        bytes.compact();

        // long
        Long l = 1L;
        bytes.writeNumber(l);
        Assert.assertEquals(l, bytes.readLong());
        Assert.assertEquals(Long.BYTES, bytes.getReadIndex());
        bytes.compact();

        // float
        Float f = 1f;
        bytes.writeNumber(f);
        Assert.assertEquals(f, bytes.readFloat());
        Assert.assertEquals(Float.BYTES, bytes.getReadIndex());
        bytes.compact();

        // double
        Double d = 1d;
        bytes.writeNumber(d);
        Assert.assertEquals(d, bytes.readDouble());
        Assert.assertEquals(Double.BYTES, bytes.getReadIndex());
        bytes.compact();

    }


    @Test
    public void writeAtoB() {
        ByteArrayData a = NettyByteArrayData.create(10);
        ByteArrayData b = NettyByteArrayData.create(10);

        // short
        int num = 1;
        a.writeNumber(num);

        b.writeBytesFrom(a);
        int integer = b.readInteger();
        Assert.assertEquals(num, integer);

    }


}