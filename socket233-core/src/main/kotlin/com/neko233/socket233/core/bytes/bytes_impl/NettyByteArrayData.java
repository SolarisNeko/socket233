package com.neko233.socket233.core.bytes.bytes_impl;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.bytes.exception.BytesException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;

/**
 * ByteBuf 内部维护 [0, readIndex, writeIndex] <br>
 * 更新数据, 有 2 种方式 = setBytes / writeBytes 只能从头开始, 会修改
 *
 * @author LuoHaoJun on 2023-06-13
 **/
public class NettyByteArrayData implements ByteArrayData {

    private final ByteBuf byteBuf;
    @Nullable
    private Integer maxSize;

    public static ByteArrayData create(int size) {
        return create(size, null);
    }

    public static ByteArrayData create(int size, Integer maxSize) {
        ByteBuf buffer;
        if (maxSize == null) {
            buffer = Unpooled.buffer(size);
        } else {
            buffer = Unpooled.buffer(size, maxSize);
        }
        return new NettyByteArrayData(buffer, maxSize);
    }

    public NettyByteArrayData(ByteBuf byteBuf, Integer maxSize) {
        this.byteBuf = byteBuf;
        this.maxSize = maxSize;
    }

    public static ByteArrayData from(ByteBuf byteBuf) {
        return new NettyByteArrayData(byteBuf, byteBuf.maxCapacity());
    }

    @Nullable
    @Override
    public Integer getMaxSize() {
        return this.maxSize;
    }

    @Override
    public ByteArrayData setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        byteBuf.capacity(maxSize);
        return this;
    }

    @Override
    public ByteArrayData createNewEmpty(int size) {
        // 创建具有指定容量的新的大端Java堆缓冲区，该缓冲区可按需无限扩展其容量。新缓冲区的ReaderIndex和WriterIndex为0。
        ByteBuf newByteBuf = Unpooled.buffer(size);
        return new NettyByteArrayData(newByteBuf, maxSize);
    }

    @Override
    public ByteArrayData createCopy() {
        ByteBuf newByteBuf = byteBuf.copy();
        return new NettyByteArrayData(newByteBuf, maxSize);
    }

    @Override
    public ByteArrayData writeBytes(int startIndex, byte[] bytes) {
        int newWriteIndex = startIndex + bytes.length;
        if (maxSize == null) {
            // 实现 upsert, 手动更新 writeIndex
            if (getWriteIndex() < newWriteIndex) {
                byteBuf.writerIndex(newWriteIndex);
            }
            byteBuf.setBytes(startIndex, bytes);
            return this;
        }

        // 限制了最大大小
        int limitWriteIndex = Math.min(newWriteIndex, this.maxSize);
        if (getWriteIndex() < limitWriteIndex) {
            byteBuf.writerIndex(limitWriteIndex);
        }
        byteBuf.setBytes(startIndex, bytes, 0, limitWriteIndex - startIndex);
        return this;
    }

    @Override
    public byte[] readBytes(int startIndex, int endIndex, boolean isUpdateReadIndex) {
        // invalid
        if (startIndex < 0 || endIndex < startIndex || endIndex > byteBuf.writerIndex()) {
            return null;
        }

        // Use the bytes array as needed
        byte[] bytes = new byte[endIndex - startIndex];
        byteBuf.getBytes(startIndex, bytes);

        if (!isUpdateReadIndex) {
            return bytes;
        }

        byteBuf.readerIndex(endIndex);

        return bytes;

    }

    @Override
    public int getCanReadSize() {
        return byteBuf.readableBytes();
    }

    @Override
    public int getCanWriteSize() {
        return byteBuf.writableBytes();
    }

    @Override
    public int getReadIndex() {
        return byteBuf.readerIndex();
    }


    @Override
    public int getWriteIndex() {
        return byteBuf.writerIndex();
    }

    @Override
    public ByteArrayData setReadWriteIndex(int readIndex, int writeIndex) {
        // Reset both read and write index to 0
        try {
            byteBuf.setIndex(readIndex, writeIndex);
        } catch (Exception e) {
            throw new BytesException(e, "超出最大容量范围 = " + maxSize);
        }
        return this;
    }


    @Override
    public ByteArrayData readTo(byte[] out) {
        byteBuf.readBytes(out);
        return this;
    }

    @Override
    public ByteArrayData readFrom(byte[] out) {
        byteBuf.getBytes(byteBuf.readerIndex(), out);
        return this;
    }

    @Override
    public ByteArrayData compact() {
        byteBuf.discardReadBytes();
        return this;
    }

    @Override
    public byte[] getByteArray() {
        return byteBuf.array();
    }
}
