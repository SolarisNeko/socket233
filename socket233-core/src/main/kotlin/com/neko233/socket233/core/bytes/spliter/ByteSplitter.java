package com.neko233.socket233.core.bytes.spliter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ByteSplitter {

    /**
     * 切单个消息包
     *
     * @param input    输入
     * @param splitEnd 切割
     * @return 结果
     */
    public static ByteSplitEndResult splitSingleByEndBytes(byte[] input, byte[] splitEnd) {
        byte[] splitedBytes = null;
        int startIndex = 0;
        int endIndex;

        while ((endIndex = indexOfPatternStartIndex(input, splitEnd, startIndex)) != -1) {
            if (endIndex > startIndex) {
                byte[] part = new byte[endIndex - startIndex];
                System.arraycopy(input, startIndex, part, 0, part.length);

                // 匹配到一个即可
                splitedBytes = part;
                startIndex = endIndex + splitEnd.length;

                break;
            }
        }

        if (splitedBytes == null) {
            // 一个消息包都无法切
            return ByteSplitEndResult.builder()
                    .splitEndByte(splitEnd)
                    .remainBytes(input)
                    .build();
        }

        // 切了一个
        ByteSplitEndResult result = ByteSplitEndResult.builder()
                .splitEndByte(splitEnd)
                .splitBytesList(Collections.singletonList(splitedBytes))
                .build();

        // 残留无法切割的部分
        if (startIndex < input.length) {
            byte[] remainBytes = new byte[input.length - startIndex];
            System.arraycopy(input, startIndex, remainBytes, 0, remainBytes.length);

            result.setRemainBytes(remainBytes);
        }

        return result;
    }


    /**
     * 以结束符作为切割
     *
     * @param input    输入
     * @param splitEnd 结束符
     * @return 切割结果
     */
    public static ByteSplitEndResult splitByEndBytes(byte[] input, byte[] splitEnd) {

        List<byte[]> splitedBytesList = new ArrayList<>();
        int startIndex = 0;
        int endIndex;

        while ((endIndex = indexOfPatternStartIndex(input, splitEnd, startIndex)) != -1) {
            if (endIndex > startIndex) {
                byte[] part = new byte[endIndex - startIndex];
                System.arraycopy(input, startIndex, part, 0, part.length);

                splitedBytesList.add(part);
            }
            startIndex = endIndex + splitEnd.length;
        }

        ByteSplitEndResult result = ByteSplitEndResult.builder()
                .splitEndByte(splitEnd)
                .splitBytesList(splitedBytesList)
                .build();

        // 残留无法切割的部分
        if (startIndex < input.length) {
            byte[] remainBytes = new byte[input.length - startIndex];
            System.arraycopy(input, startIndex, remainBytes, 0, remainBytes.length);

            result.setRemainBytes(remainBytes);
        }

        return result;
    }

    /**
     * 返回匹配到 pattern 的最后一个 byte 的 index
     *
     * @param source
     * @param pattern
     * @param startIndex
     * @return
     */
    public static int indexOfPatternStartIndex(byte[] source, byte[] pattern, int startIndex) {
        int[] failure = computeFailure(pattern);

        int j = 0;
        for (int i = startIndex; i < source.length; i++) {
            while (j > 0 && pattern[j] != source[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == source[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];
        int j = 0;

        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }
}
