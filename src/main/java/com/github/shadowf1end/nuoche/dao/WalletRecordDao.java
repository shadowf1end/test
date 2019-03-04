package com.github.shadowf1end.nuoche.dao;

import com.github.shadowf1end.nuoche.base.BaseDao;
import com.github.shadowf1end.nuoche.entity.WalletRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author su
 */
public interface WalletRecordDao extends BaseDao<WalletRecord, Integer> {
    List<WalletRecord> findAllByWechatUserIdAndVerifyFlagAndType(Long wechatUserId, Integer verifyFlag, Integer type, Pageable pageable);

    List<WalletRecord> findAllByWechatUserIdAndVerifyFlagAndTypeAndUpdateTimeBetween(Long wechatUserId, Integer verifyFlag, Integer type, LocalDateTime updateTime, LocalDateTime updateTime2, Pageable pageable);

    @Query(value = "select sum(amount) from WalletRecord where wechatUserId = :wechatUserId and verifyFlag = :verifyFlag and type = :t")
    Integer sumByWechatUserIdAndVerifyFlagAndTypeIn(@Param("wechatUserId") Long wechatUserId, @Param("verifyFlag") Integer verifyFlag, @Param("t") Integer type);

    Integer countByVerifyFlagAndWechatUserId(Integer verifyFlag, Long wechatUserId);
}
