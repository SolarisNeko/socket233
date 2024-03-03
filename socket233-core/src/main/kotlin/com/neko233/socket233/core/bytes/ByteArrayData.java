package com.neko233.socket233.core.bytes;

import com.neko233.socket233.core.annotation.Out;
import com.neko233.socket233.core.bytes.exception.BytesException;
import com.neko233.socket233.core.bytes.spliter.ByteSplitEndResult;
import com.neko233.socket233.core.bytes.spliter.ByteSplitter;
import com.neko233.socket233.core.msgpacket.MessagePacketHandler;
import com.neko233.socket233.core.utils.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * byte[] 包装操作器
 * <p>
 * 设计目的:
 * 1. 统一 byte[] 操作 API
 * 2. 少内存拷贝、分配、new;
 * 3. 紧凑;
 * 4. 自动化 = 自动扩容；
 *
 * @author LuoHaoJun on 2023-06-13
 **/
public interface ByteArrayData {

    /**
     * 获取最大容量大小
     *
     * @return 大小限制, 如果不存在为 null
     */
    @Nullable
    Integer getMaxSize();


    /**
     * 基于 byte[] 作为结束符进行切割, 切割后, 不保留原有的 split 部分
     *
     * @param splitEndByteArray 结束的 byte[]
     * @return 切割后的内容
     */
    default List<byte[]> splitEndByBytes(byte[] splitEndByteArray) {
        if (splitEndByteArray == null) {
            return emptyList();
        }
        byte[] byteArray = this.readBytes();
        final ByteSplitEndResult result = ByteSplitter.splitByEndBytes(byteArray, splitEndByteArray);

        byte[] remainBytes = result.getRemainBytes();
        final List<byte[]> splitBytesList = ObjectUtils.getOrDefault(result.getSplitBytesList(),
                emptyList());

        if (remainBytes == null) {
            return splitBytesList;
        }

        // 将剩余无法切割的内容, 重新写入回去
        this.compact();
        this.writeBytes(remainBytes);

        return splitBytesList;
    }

    @Nullable
    default byte[] splitSingleEndByBytes(byte[] splitEndByteArray) {

        byte[] cloneBytes = this.readBytes(false);
        int endIndex = ByteSplitter.indexOfPatternStartIndex(cloneBytes, splitEndByteArray, 0);
        if (endIndex != -1) {
            byte[] output = new byte[endIndex];
            System.arraycopy(cloneBytes, 0, output, 0, endIndex);

            // 修改 readIndex
            this.readBytes(endIndex + splitEndByteArray.length);

            return output;
        }

        return null;

//        if (splitEndByteArray == null) {
//            return null;
//        }
//        byte[] byteArray = this.readBytes();
//        final ByteSplitEndResult result = ByteSplitter.splitSingleByEndBytes(byteArray, splitEndByteArray);
//
//        byte[] remainBytes = result.getRemainBytes();
//        final List<byte[]> outList = ObjectUtils233.getOrDefault(result.getSplitBytesList(),
//                Collections.emptyList());
//
//        // 将剩余无法切割的内容, 重新写入回去
//        if (remainBytes != null) {
//            this.compact();
//            this.writeBytes(remainBytes);
//        }
//
//        if (CollectionUtils.isEmpty(outList)) {
//            return null;
//        }
//
//
//        return ListUtils233.get(outList, 0);
    }

    default List<String> splitEndByString(String splitFullText) {
        if (splitFullText == null) {
            return emptyList();
        }
        List<byte[]> bytes = this.splitEndByBytes(splitFullText.getBytes(StandardCharsets.UTF_8));
        if (bytes == null) {
            return emptyList();
        }
        return bytes.stream()
                .map(byteArray -> new String(byteArray, StandardCharsets.UTF_8))
                .collect(Collectors.toList());
    }

    ByteArrayData setMaxSize(int maxSize);

    ByteArrayData createNewEmpty(int size);

    ByteArrayData createCopy();

    default ByteArrayData writeByte(byte b) {
        this.writeBytes(b);
        return this;
    }

    /**
     * 写
     */
    default ByteArrayData writeNumber(Number number) {
        if (number == null) {
            return this;
        }
        byte[] array = null;
        if (number instanceof Byte) {
            array = ByteBuffer.allocate(Byte.BYTES)
                    .put((Byte) number)
                    .array();
        } else if (number instanceof Short) {
            array = ByteBuffer.allocate(Short.BYTES)
                    .putShort((Short) number)
                    .array();
        } else if (number instanceof Integer) {
            array = ByteBuffer.allocate(Integer.BYTES)
                    .putInt((Integer) number)
                    .array();
        } else if (number instanceof Long) {
            array = ByteBuffer.allocate(Long.BYTES)
                    .putLong((Long) number)
                    .array();
        } else if (number instanceof Float) {
            array = ByteBuffer.allocate(Float.BYTES)
                    .putFloat((Float) number)
                    .array();
        } else if (number instanceof Double) {
            array = ByteBuffer.allocate(Double.BYTES)
                    .putDouble((Double) number)
                    .array();
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + number.getClass().getName());
        }
        if (array == null) {
            return this;
        }
        return writeBytes(array);
    }

    default ByteArrayData writeBytes(byte b) {
        return this.writeBytes(new byte[]{b});
    }

    /**
     * 将 input 所有数据写入到自己这里, 会修改 input 的 read index, 并且会修改自己的 write index
     *
     * @param input 输入
     * @return this
     */
    default ByteArrayData writeBytesFrom(ByteArrayData input) {
        byte[] allBytes = input.readBytes();
        return this.writeBytes(allBytes);
    }

    default ByteArrayData writeBytes(byte[] bytes) {
        int nowWriteIndex = getWriteIndex();
        return writeBytes(nowWriteIndex, bytes);
    }

    ByteArrayData writeBytes(int startIndex, byte[] bytes);

    default ByteArrayData writeString(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        this.writeBytes(bytes);
        return this;
    }

    /**
     * 读取所有 bytes, 会修改状态
     */
    @Nullable
    default byte[] readBytes() {
        int readIndex = getReadIndex();
        return readBytes(true);
    }

    @Nullable
    default byte[] readBytes(boolean isUpdateReadIndex) {
        int readIndex = getReadIndex();
        return readBytes(readIndex, getWriteIndex(), isUpdateReadIndex);
    }

    default byte[] readBytes(int size) {
        return readBytes(size, true);
    }

    default byte[] readBytes(int size, boolean isUpdateReadIndex) {
        int readIndex = getReadIndex();
        return readBytes(readIndex, readIndex + size, isUpdateReadIndex);
    }

    default byte[] readBytes(int startIndex, int endIndex) {
        return readBytes(startIndex, endIndex, true);
    }

    byte[] readBytes(int startIndex, int endIndex, boolean isUpdateReadIndex);

    @Nullable
    default String readString() {
        byte[] bytes = readBytes();
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * @return 已读大小
     */
    default int getHaveReadSize() {
        return getReadIndex();
    }

    /**
     * @return 剩余可读大小
     */
    int getCanReadSize();

    /**
     * @return 剩余可写大小
     */
    int getCanWriteSize();

    /**
     * @return 读下标
     */
    int getReadIndex();

    default ByteArrayData addReadIndex(int size) {
        int readIndex = getReadIndex();
        setReadIndex(readIndex + size);
        return this;
    }

    default ByteArrayData minusReadIndex(int size) {
        return addReadIndex(-size);
    }

    default ByteArrayData addWriteIndex(int size) {
        int writeIndex = getWriteIndex();
        setWriteIndex(writeIndex + size);
        return this;
    }

    default ByteArrayData minusWriteIndex(int size) {
        return addWriteIndex(-size);
    }

    default ByteArrayData setReadIndex(int readIndex) {
        setReadWriteIndex(readIndex, getWriteIndex());
        return this;
    }

    default ByteArrayData resetReadIndex() {
        setReadWriteIndex(0, getWriteIndex());
        return this;
    }

    default ByteArrayData resetReadWriteIndex() {
        setReadWriteIndex(0, 0);
        return this;
    }

    /**
     * @return 写下标
     */
    int getWriteIndex();

    default ByteArrayData setWriteIndex(int writeIndex) {
        setReadWriteIndex(getReadIndex(), writeIndex);
        return this;
    }


    ByteArrayData setReadWriteIndex(int readIndex, int writeIndex) throws BytesException;


    /**
     * 读 + 修改 readIndex
     *
     * @param out 输出的 byte[]
     * @return this
     */
    ByteArrayData readTo(@Out byte[] out);

    default byte[] readTo(int byteSize) {
        byte[] out = new byte[byteSize];
        this.readTo(out);
        return out;
    }

    default String readToString(int byteSize) {
        byte[] bytes = readTo(byteSize);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    default Byte readByte() {
        // bytes
        byte[] bytes = this.readTo(1);
        if (bytes == null) {
            return null;
        }
        return bytes[0];
    }

    default Short readShort() {
        return readShort(true);
    }


    default Short readShort(boolean isUpdateReadIndex) {
        if (isUpdateReadIndex) {
            // 4 bytes
            byte[] bytes = this.readTo(Short.BYTES);
            return ByteBuffer.wrap(bytes).getShort();
        }
        // 4 bytes
        byte[] bytes = this.readFrom(Short.BYTES);
        return ByteBuffer.wrap(bytes).getShort();
    }

    /**
     * 读一个 int, 修改 read index
     *
     * @return int
     */
    default Integer readInteger() {
        return readInteger(true);
    }

    /**
     * 偷偷读一个 int, 不修改 read index
     *
     * @return int
     */
    default Integer readInteger(boolean isUpdateReadIndex) {
        if (isUpdateReadIndex) {
            // 4 bytes
            byte[] bytes = this.readTo(Integer.BYTES);
            return ByteBuffer.wrap(bytes).getInt();
        }
        // 4 bytes
        byte[] bytes = this.readFrom(Integer.BYTES);
        return ByteBuffer.wrap(bytes).getInt();
    }

    default Long readLong() {
        return readLong(true);
    }

    default Long readLong(boolean isUpdateReadIndex) {
        if (isUpdateReadIndex) {
            byte[] bytes = this.readTo(Long.BYTES);
            return ByteBuffer.wrap(bytes).getLong();
        }
        // 8 bytes
        byte[] bytes = this.readFrom(Long.BYTES);
        return ByteBuffer.wrap(bytes).getLong();
    }


    default Float readFloat() {
        // 4 bytes
        byte[] bytes = this.readTo(Float.BYTES);
        return ByteBuffer.wrap(bytes).getFloat();
    }

    default Float readFloat(boolean isUpdateReadIndex) {
        // 4 bytes
        if (isUpdateReadIndex) {
            byte[] bytes = this.readTo(Float.BYTES);
            return ByteBuffer.wrap(bytes).getFloat();
        }
        byte[] bytes = this.readTo(Float.BYTES);
        return ByteBuffer.wrap(bytes).getFloat();
    }

    default Double readDouble() {
        // 8 bytes
        byte[] bytes = this.readTo(Double.BYTES);
        return ByteBuffer.wrap(bytes).getDouble();
    }

    default Double readDouble(boolean isUpdateReadIndex) {
        if (isUpdateReadIndex) {
            byte[] bytes = this.readTo(Double.BYTES);
            return ByteBuffer.wrap(bytes).getDouble();
        }
        // 8 bytes
        byte[] bytes = this.readFrom(Double.BYTES);
        return ByteBuffer.wrap(bytes).getDouble();
    }


    /**
     * 读数据 + 不修改 readIndex (from)
     *
     * @param out 输出的 byte[] 的引用 | 他的长度会控制读取内容
     * @return this
     */
    ByteArrayData readFrom(@Out byte[] out);

    /**
     * 不修改 readIndex 读取 byteSize 大小的 byte[], 深拷贝
     *
     * @param byteSize 大小
     * @return byte[]
     */
    @NotNull
    default byte[] readFrom(int byteSize) {
        byte[] out = new byte[byteSize];
        readFrom(out);
        return out;
    }

    default String readStringFrom(int byteSize) {
        byte[] bytes = readFrom(byteSize);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 整理 byte[] 数据 <br>
     * 会修改 readIndex, writeIndex <br>
     * 将 [0, have Read index] 部分丢弃
     *
     * @return this
     */
    ByteArrayData compact();

    /**
     * 委托 handler 进行 【分包粘包】 处理
     *
     * @param handler 处理器
     * @return 单个包的 byte[], 如果不足以分包, 则 return null
     */
    @Nullable
    default byte[] getFullMessagePacket(MessagePacketHandler handler) {
        return handler.handle(this);
    }

    @NotNull
    default List<byte[]> getAllFullMessagePacket(MessagePacketHandler handler) {
        return getAllFullMessagePacket(handler, 10);
    }

    @NotNull
    default List<byte[]> getAllFullMessagePacket(MessagePacketHandler handler, int count) {
        List<byte[]> byteList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            byte[] bytes = handler.handle(this);
            if (bytes == null) {
                break;
            }
            byteList.add(bytes);
        }
        return byteList;
    }


    /**
     * 可读内容 = [readIndex, writeIndex] -> [0, readIndex]
     */
    default ByteArrayData flip() {
        int readIndex = getReadIndex();
        setWriteIndex(readIndex);
        setReadIndex(0);
        return this;
    }

    /**
     * 获取原始数组 | 尽量少这样操作
     *
     * @return 数组
     */
    byte[] getByteArray();

    default byte[] getByteArrayCopy() {
        byte[] byteArray = getByteArray();
        if (byteArray == null) {
            return null;
        }
        return byteArray.clone();
    }

    /**
     * 子 byte[]
     *
     * @param startIndex 开始位置
     * @param endIndex   结束位置
     * @return byte[]
     */
    default byte[] getSubByteArray(int startIndex, int endIndex) {
        byte[] byteArray = getByteArray();
        byte[] out = new byte[endIndex - startIndex + 1];
        System.arraycopy(byteArray, startIndex, out, 0, out.length);
        return out;
    }

}
