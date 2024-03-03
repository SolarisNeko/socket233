package com.neko233.socket233.core.bytes.bytes_impl;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.bytes.exception.BytesException;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author LuoHaoJun on 2023-06-14
 **/
public class OriginalByteArrayData implements ByteArrayData {

    private int readIndex;
    private int writeIndex;
    private byte[] data;
    private int initSize;
    private Integer maxSize;

    public static OriginalByteArrayData create(int initSize) {
        return new OriginalByteArrayData(new byte[initSize], 0, 0, null);
    }

    public static OriginalByteArrayData create(int initSize, Integer maxSize) {
        return new OriginalByteArrayData(new byte[initSize], 0, 0, maxSize);
    }

    public static OriginalByteArrayData create(byte[] data) {
        return new OriginalByteArrayData(data, 0, 0, null);
    }

    public static OriginalByteArrayData create(byte[] data, int readIndex, int writeIndex) {
        return new OriginalByteArrayData(data, readIndex, writeIndex, null);
    }

    private OriginalByteArrayData(byte[] data, int readIndex, int writeIndex, Integer maxSize) {
        this.data = data;
        this.initSize = data.length;
        this.readIndex = readIndex;
        this.writeIndex = writeIndex;
        this.maxSize = maxSize == null ? null : Math.max(maxSize, data.length);
    }

    @Nullable
    @Override
    public Integer getMaxSize() {
        return null;
    }

    @Override
    public ByteArrayData setMaxSize(int maxSize) {
        return null;
    }

    @Override
    public ByteArrayData createNewEmpty(int size) {
        byte[] bytes = new byte[size];
        return new OriginalByteArrayData(bytes, 0, 0, null);
    }

    @Override
    public ByteArrayData createCopy() {
        byte[] newBytes = Arrays.copyOf(data, data.length);
        return new OriginalByteArrayData(newBytes, this.readIndex, this.writeIndex, this.maxSize);
    }

    @Override
    public ByteArrayData writeBytes(int startIndex, byte[] bytes) {
        if (this.maxSize == null) {
            // 自动扩容
            if (1 + startIndex + bytes.length > this.data.length) {
                // 扩容
                this.data = Arrays.copyOf(data, data.length + bytes.length);
            }
            System.arraycopy(bytes, 0, this.data, startIndex, bytes.length);
            // 修改索引
            this.writeIndex += bytes.length;
            return this;
        }

        // 最大容量限制
        if (1 + startIndex + bytes.length > maxSize) {
            // 扩容
            this.data = Arrays.copyOf(data, data.length + bytes.length);
        }
        System.arraycopy(bytes, 0, this.data, startIndex, bytes.length);
        // 修改索引
        this.writeIndex += bytes.length;
        return this;
    }

    @Override
    public byte[] readBytes(int startIndex, int endIndex, boolean isUpdateReadIndex) {
        byte[] outBytes = Arrays.copyOfRange(data, startIndex, endIndex);
        if (!isUpdateReadIndex) {
            return outBytes;
        }
        // 读状态更新
        this.readIndex = Math.min(endIndex, this.writeIndex);
        return outBytes;
    }

    @Override
    public int getCanReadSize() {
        return writeIndex - readIndex;
    }

    @Override
    public int getCanWriteSize() {
        return data.length - 1 - writeIndex;
    }

    @Override
    public int getReadIndex() {
        return this.readIndex;
    }

    @Override
    public int getWriteIndex() {
        return this.writeIndex;
    }

    @Override
    public ByteArrayData setReadWriteIndex(int newReadIndex, int newWriteIndex) {
        this.readIndex = Math.max(0, newReadIndex);
        if (this.maxSize == null) {
            this.writeIndex = Math.min(newWriteIndex, data.length - 1);
            return this;
        }

        if (newWriteIndex > this.maxSize) {
            throw new BytesException("设置的 writeIndex 超出最大容量. writeIndex = " + newWriteIndex + "maxSize = " + this.maxSize);
        }

        this.writeIndex = newWriteIndex;
        return this;

    }

    @Override
    public ByteArrayData readTo(byte[] out) {
        if (out == null) {
            return null;
        }
        int canReadSize = Math.min(out.length, getCanReadSize());
        System.arraycopy(data, readIndex, out, 0, canReadSize);

        // 读完后, 状态更新
        this.readIndex = this.readIndex + canReadSize;
        return this;
    }

    @Override
    public ByteArrayData readFrom(byte[] out) {
        if (out == null) {
            return null;
        }
        int canReadSize = Math.min(out.length, getCanReadSize());
        System.arraycopy(data, readIndex, out, 0, canReadSize);

        return this;
    }

    @Override
    public ByteArrayData compact() {
        // +1 for 当前位置
        int newLength = writeIndex - readIndex + 1;
        // 新的空间
        int newBytesCapacity = this.maxSize == null ? newLength : this.maxSize;
        if (newLength <= 0) {
            // 重置
            Arrays.fill(this.data, (byte) 0);
            this.readIndex = 0;
            this.writeIndex = 0;
            return this;
        }


        // 使用数组内容移动, 代替 System.arrayCopy 做 compact
        int newWriteIndex = compactDataInOriginalArray();

        // 内存拷贝 | 内存浪费, 只是抛弃了 readIndex 已读的数据
//        byte[] newBytes = new byte[newBytesCapacity];
//        System.arraycopy(this.data, readIndex, newBytes, 0, newLength);
//        this.data = newBytes;
//        this.writeIndex = newBytes.length - 1;

        this.readIndex = 0;
        this.writeIndex = newWriteIndex;
        return this;
    }

    /**
     * 在原来的数组中, 压缩数据. newLength = [read index, write index] +1 （包含 readIndex 本身） 到 byte[0, newLength]
     *
     * @return newWriteIndex
     */
    private int compactDataInOriginalArray() {
        // +1 for 当前位置
        int newLength = writeIndex - readIndex + 1;

        int newWriteIndex = newLength - 1;
        for (int i = 0; i < newLength; i++) {
            this.data[i] = this.data[readIndex + i];
        }
        for (int i = newWriteIndex; i < data.length; i++) {
            this.data[newWriteIndex] = 0;
        }
        return newWriteIndex;
    }

    @Override
    public byte[] getByteArray() {
        return this.data;
    }
}
