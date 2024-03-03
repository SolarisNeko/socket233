package com.neko233.socket233.core.bytes.spliter;


import com.neko233.socket233.core.utils.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author LuoHaoJun on 2023-06-21
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ByteSplitEndResult {

    // 以什么作为 byte[] 结束符
    private byte[] splitEndByte;
    // 切割出来的 bytes, 不带有结束符
    private List<byte[]> splitBytesList;
    // 剩余没得切割的 bytes
    private byte[] remainBytes;

    public int getSplitSize() {
        if (CollectionUtils.isEmpty(splitBytesList)) {
            return 0;
        }
        return splitBytesList.size();
    }
}
