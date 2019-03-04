package com.github.shadowf1end.nuoche.service;

import com.github.shadowf1end.nuoche.entity.WalletRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author su
 */
public interface WalletRecordService {
    List<WalletRecord> listByWechatUserIdAndVerifyFlagAndType(Long wechatUserId, Integer verifyFlag, Integer type, Integer page, Integer size);

    List<WalletRecord> listByWechatUserIdAndVerifyFlagAndTypeAndUpdateTimeBetween(Long wechatUserId, Integer verifyFlag, Integer type, LocalDateTime updateTime, LocalDateTime updateTime2, Integer page, Integer size);

    Integer sumByWechatUserIdAndVerifyFlagAndTypeIn(Long wechatUserId, Integer verifyFlag, Integer type);

    Integer countByVerifyFlagAndWechatUserId(Integer verifyFlag, Long wechatUserId);

    WalletRecord save(WalletRecord walletRecord);
}
