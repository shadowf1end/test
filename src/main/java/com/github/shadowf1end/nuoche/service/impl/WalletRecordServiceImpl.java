package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.WalletRecordDao;
import com.github.shadowf1end.nuoche.entity.WalletRecord;
import com.github.shadowf1end.nuoche.service.WalletRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author su
 */
@Service("walletRecordService")
public class WalletRecordServiceImpl implements WalletRecordService {

    private final WalletRecordDao walletRecordDao;

    @Autowired
    public WalletRecordServiceImpl(WalletRecordDao walletRecordDao) {
        this.walletRecordDao = walletRecordDao;
    }

    @Override
    public List<WalletRecord> listByWechatUserIdAndVerifyFlagAndType(Long wechatUserId, Integer verifyFlag, Integer type, Integer page, Integer size) {
        return walletRecordDao.findAllByWechatUserIdAndVerifyFlagAndType(wechatUserId, verifyFlag, type, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime", "createTime")));
    }

    @Override
    public List<WalletRecord> listByWechatUserIdAndVerifyFlagAndTypeAndUpdateTimeBetween(Long wechatUserId, Integer verifyFlag, Integer type, LocalDateTime updateTime, LocalDateTime updateTime2, Integer page, Integer size) {
        return walletRecordDao.findAllByWechatUserIdAndVerifyFlagAndTypeAndUpdateTimeBetween(wechatUserId, verifyFlag, type, updateTime, updateTime2, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime", "createTime")));
    }

    @Override
    public Integer sumByWechatUserIdAndVerifyFlagAndTypeIn(Long wechatUserId, Integer verifyFlag, Integer type) {
        return walletRecordDao.sumByWechatUserIdAndVerifyFlagAndTypeIn(wechatUserId, verifyFlag, type);
    }

    @Override
    public Integer countByVerifyFlagAndWechatUserId(Integer verifyFlag, Long wechatUserId) {
        return walletRecordDao.countByVerifyFlagAndWechatUserId(verifyFlag, wechatUserId);
    }

    @Override
    public WalletRecord save(WalletRecord walletRecord) {
        return walletRecordDao.saveAndFlush(walletRecord);
    }
}
