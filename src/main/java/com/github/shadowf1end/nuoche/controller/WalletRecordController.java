package com.github.shadowf1end.nuoche.controller;

import com.github.shadowf1end.nuoche.base.BaseController;
import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import com.github.shadowf1end.nuoche.entity.WalletRecord;
import com.github.shadowf1end.nuoche.entity.WechatUser;
import com.github.shadowf1end.nuoche.service.WalletRecordService;
import com.github.shadowf1end.nuoche.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author su
 */
@RestController
@RequestMapping("wallet")
public class WalletRecordController extends BaseController {

    private final HttpServletRequest request;
    private final WalletRecordService walletRecordService;
    private final WechatUserService wechatUserService;

    @Autowired
    public WalletRecordController(HttpServletRequest request, WalletRecordService walletRecordService, WechatUserService wechatUserService) {
        this.request = request;
        this.walletRecordService = walletRecordService;
        this.wechatUserService = wechatUserService;
    }

    /**
     * 钱包记录
     * @param type
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public Result<Object> list(Integer type, Integer page, Integer size, String startTime, String endTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (page == null || size == null) {
            page = 0;
            size = 10;
        }
        if (startTime != null && endTime != null) {
            return new ResultUtil<>().setData(walletRecordService.listByWechatUserIdAndVerifyFlagAndTypeAndUpdateTimeBetween(getUserId(request), 1, type, LocalDateTime.parse(startTime + " 00:00:00", df), LocalDateTime.parse(endTime + " 23:59:59", df), page, size));
        } else {
            return new ResultUtil<>().setData(walletRecordService.listByWechatUserIdAndVerifyFlagAndType(getUserId(request), 1, type, page, size));
        }
    }

    @RequestMapping(value = "sum", method = RequestMethod.POST)
    public Result<Object> sum(Integer type) {
        return new ResultUtil<>().setData(walletRecordService.sumByWechatUserIdAndVerifyFlagAndTypeIn(getUserId(request), 1, type));
    }

    /**
     * 申请提现
     * @param amount
     * @return
     */
    @RequestMapping(value = "cash", method = RequestMethod.POST)
    public Result<Object> cash(Integer amount) {
        if (amount < 200) {
            return new ResultUtil<>().setErrorMsg("提现金额最少为2元");
        }
        Long wechatUserId = getUserId(request);
        if (walletRecordService.countByVerifyFlagAndWechatUserId(0, wechatUserId) > 0) {
            return new ResultUtil<>().setErrorMsg("正在审核中");
        }
        WechatUser wechatUser = wechatUserService.find(wechatUserId);
        if (wechatUser.getWallet() < amount) {
            return new ResultUtil<>().setErrorMsg("余额不足");
        }

        WalletRecord walletRecord = new WalletRecord();
        walletRecord.setAmount(amount);
        walletRecord.setDes("提现");
        walletRecord.setType(3);
        walletRecord.setVerifyFlag(0);
        walletRecord.setWechatUserId(wechatUserId);
        walletRecord.setCurWallet(wechatUser.getWallet() - amount);
        walletRecord.setPreWallet(wechatUser.getWallet());
        walletRecordService.save(walletRecord);

        return new ResultUtil<>().setSuccessMsg("已提交申请");
    }
}
