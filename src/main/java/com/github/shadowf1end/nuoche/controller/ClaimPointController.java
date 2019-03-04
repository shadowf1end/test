package com.github.shadowf1end.nuoche.controller;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.shadowf1end.nuoche.base.BaseController;
import com.github.shadowf1end.nuoche.common.util.IpInfoUtil;
import com.github.shadowf1end.nuoche.common.util.MapUtil;
import com.github.shadowf1end.nuoche.common.util.RedisUtil;
import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.ClaimPointVo;
import com.github.shadowf1end.nuoche.common.vo.Result;
import com.github.shadowf1end.nuoche.entity.*;
import com.github.shadowf1end.nuoche.service.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author su
 */
@RestController
@RequestMapping("claimPoint")
@Slf4j
public class ClaimPointController extends BaseController {

    private final ClaimPointService claimPointService;
    private final HttpServletRequest request;
    private final RedisUtil redisUtil;
    private final WechatUserService wechatUserService;
    private final WxPayService wxPayService;
    private final PartnerService partnerService;
    private final WalletRecordService walletRecordService;
    private final BundlingService bundlingService;
    private final WxMaMsgService wxMaMsgService;


    @Autowired
    public ClaimPointController(ClaimPointService claimPointService, HttpServletRequest request, RedisUtil redisUtil, WechatUserService wechatUserService, WxPayService wxPayService, PartnerService partnerService, WalletRecordService walletRecordService, BundlingService bundlingService, WxMaMsgService wxMaMsgService) {
        this.claimPointService = claimPointService;
        this.request = request;
        this.redisUtil = redisUtil;
        this.wechatUserService = wechatUserService;
        this.wxPayService = wxPayService;
        this.partnerService = partnerService;
        this.walletRecordService = walletRecordService;
        this.bundlingService = bundlingService;
        this.wxMaMsgService = wxMaMsgService;
    }

    /**
     * 商家推送消息
     *
     * @param claimPointId
     * @param startTime
     * @param endTime
     * @param title
     * @param explain
     * @return
     */
    @RequestMapping(value = "push", method = RequestMethod.POST)
    public Result<Object> push(Long claimPointId, String startTime, String endTime, String title, String explain) {
        String key = "push:" + claimPointId;
        if (redisUtil.hasKey(key)) {
            return new ResultUtil<>().setErrorMsg("7天内仅可推送一次");
        }

        List<Long> wechatUserIds = bundlingService.listWechatUserIdByClaimPointId(claimPointId);
//        ClaimPoint claimPoint = claimPointService.find(claimPointId);
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setTemplateId("KIBw-opGhBgwKwbcClmIkI90GEGodZGLq7HYyQIiGoA");
        List<WxMaTemplateData> list = new ArrayList<>();
        list.add(new WxMaTemplateData("keyword3", startTime));
        list.add(new WxMaTemplateData("keyword4", endTime));
        list.add(new WxMaTemplateData("keyword1", title));
        list.add(new WxMaTemplateData("keyword2", explain));
        wxMaTemplateMessage.setData(list);
        for (Long wechatUserId : wechatUserIds) {
            String formId = wechatUserService.getFormId(wechatUserId);
            if (formId == null) {
                continue;
            }
            WechatUser wechatUser = wechatUserService.find(wechatUserId);
            wxMaTemplateMessage.setFormId(formId);
            wxMaTemplateMessage.setToUser(wechatUser.getOpenId());
            wxMaTemplateMessage.setPage("pages/Index");

            try {
                wxMaMsgService.sendTemplateMsg(wxMaTemplateMessage);
            } catch (WxErrorException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }

        redisUtil.set(key, String.valueOf(System.currentTimeMillis()));
        redisUtil.expireAt(key, new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        return new ResultUtil<>().setSuccessMsg("推送成功");
    }

    /**
     * 申请领取点
     *
     * @param claimPoint
     * @return
     * @throws WxPayException
     */
    @RequestMapping(value = "apply", method = RequestMethod.POST)
    public Result<Object> apply(ClaimPoint claimPoint) throws WxPayException {
        WechatUser wechatUser = wechatUserService.find(getUserId(request));
        claimPoint.setWechatUserId(getUserId(request));

        if (claimPoint.getRecId() != null) {
            WechatUser recUser = wechatUserService.find(claimPoint.getRecId());
//            if ("claim".equals(recUser.getRole())) {
//                ClaimPoint claim = claimPointService.findByWechatUserIdAndState(recUser.getId(), 1);
//                if (claim.getRecId() != null) {
//                    Partner partner = partnerService.findByWechatUserId(claim.getRecId());
//                    if (partner != null && partner.getId().equals(claim.getPartnerId())) {
//                        claimPoint.setPartnerId(partner.getId());
//                    }
//                }
//            } else
            if ("user".equals(recUser.getRole())) {
                claimPoint.setRecId(null);
            } else if ("partner".equals(recUser.getRole()) || "all".equals(recUser.getRole())) {
                Partner partner = partnerService.findByWechatUserId(recUser.getId());
                claimPoint.setPartnerId(partner.getId());
            } else if ("claim".equals(recUser.getRole())) {
                List<ClaimPoint> claimPoints = claimPointService.findByWechatUserIdAndState(recUser.getId(), 1);
                if (claimPoints.size() > 0) {
                    for (ClaimPoint claimPoint1 : claimPoints) {
                        if (claimPoint1.getPartnerId() != null) {
                            claimPoint.setPartnerId(claimPoint1.getPartnerId());
                            break;
                        }
                        claimPoint.setRecId(null);
                    }
                } else {
                    claimPoint.setRecId(null);
                }
            }
        } else if (wechatUser.getRecId() != null) {
            WechatUser recUser = wechatUserService.find(wechatUser.getRecId());
            if (recUser.getRole().equals("partner") || recUser.getRole().equals("all")) {
                Partner partner = partnerService.findByWechatUserId(recUser.getId());
                claimPoint.setRecId(wechatUser.getRecId());
                claimPoint.setPartnerId(partner.getId());
            }
        }
        claimPointService.save(claimPoint);

        String outTradeNo = claimPoint.getId().toString();
        String body = "领取点";
        String notifyUrl = "https://127.0.0.1";
        String openId = wechatUserService.find(getUserId(request)).getOpenId();
        String ip = IpInfoUtil.getIpAddr(request);
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(outTradeNo);
        if (redisUtil.hasKey("application_price")) {
            orderRequest.setTotalFee(Integer.valueOf(redisUtil.get("application")));
        } else {
            orderRequest.setTotalFee(288);
        }
//        orderRequest.setTotalFee(1);
        orderRequest.setOpenid(openId);
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setNotifyUrl(notifyUrl);
        orderRequest.setTradeType("JSAPI");
        WxPayMpOrderResult result = wxPayService.createOrder(orderRequest);

        Map<String, Object> map = new HashMap<String, Object>(2) {
            {
                put("pay", result);
                put("no", claimPoint.getId().toString());
            }
        };
        return new ResultUtil<>().setData(map);
    }

    /**
     * 申请领取点微信支付查询
     *
     * @param id
     * @return
     * @throws WxPayException
     */
    @RequestMapping(value = "query", method = RequestMethod.POST)
    public Result<Object> query(Long id) throws WxPayException {
        WxPayOrderQueryResult result = wxPayService.queryOrder(null, id.toString());
        if ("SUCCESS".equals(result.getResultCode().toUpperCase())
                && "SUCCESS".equals(result.getReturnCode().toUpperCase())
                && "SUCCESS".equals(result.getTradeState().toUpperCase())) {
            ClaimPoint claimPoint = claimPointService.find(id);
            claimPoint.setState(1);
            claimPointService.save(claimPoint);

            WechatUser wechatUser = wechatUserService.find(claimPoint.getWechatUserId());
            if ("partner".equals(wechatUser.getRole())) {
                wechatUser.setRole("all");
                wechatUserService.save(wechatUser);
            } else if ("user".equals(wechatUser.getRole())) {
                wechatUser.setRole("claim");
                wechatUserService.save(wechatUser);
            }

            if (claimPoint.getRecId() != null) {
                WechatUser recUser = wechatUserService.find(claimPoint.getRecId());
                WalletRecord walletRecord1 = new WalletRecord();
                walletRecord1.setPreWallet(recUser.getWallet());
                Integer rewardAmount;
                if (redisUtil.hasKey("reward_amount")) {
                    rewardAmount = Integer.valueOf(redisUtil.get("reward_amount"));
                } else {
                    rewardAmount = 88;
                }
                recUser.setWallet(recUser.getWallet() + rewardAmount);
                wechatUserService.save(recUser);
                walletRecord1.setCurWallet(recUser.getWallet());
                walletRecord1.setWechatUserId(recUser.getId());
                walletRecord1.setVerifyFlag(1);
                walletRecord1.setType(0);
                walletRecord1.setDes("推荐领取点" + claimPoint.getId());
                walletRecord1.setAmount(rewardAmount);
                walletRecordService.save(walletRecord1);

                try {
                    sendTemplateMessage(wechatUserService.getFormId(recUser.getId()), recUser.getOpenId(), rewardAmount);
                } catch (WxErrorException e) {
                    return new ResultUtil<>().setSuccessMsg("支付成功");
                }
//                if (claimPoint.getPartnerId() != null) {
//                    Partner partner = partnerService.find(claimPoint.getPartnerId());
//                    if (!partner.getWechatUserId().equals(claimPoint.getRecId())) {
//                        WechatUser partnerUser = wechatUserService.find(partner.getWechatUserId());
//                        WalletRecord walletRecord2 = new WalletRecord();
//                        walletRecord2.setPreWallet(partnerUser.getWallet());
//                        partnerUser.setWallet(partnerUser.getWallet() + 5000);
//                        wechatUserService.save(partnerUser);
//                        walletRecord2.setCurWallet(partnerUser.getWallet());
//                        walletRecord2.setAmount(5000);
//                        ClaimPoint claimPoint1 = claimPointService.findByWechatUserIdAndState(claimPoint.getRecId(), 1);
//                        walletRecord2.setDes("领取点" + claimPoint1.getId() + "推荐领取点" + claimPoint.getId());
//                        walletRecord2.setType(1);
//                        walletRecord2.setVerifyFlag(1);
//                        walletRecord2.setWechatUserId(partnerUser.getId());
//                        walletRecordService.save(walletRecord2);
//                    }
//                }
            }

            return new ResultUtil<>().setSuccessMsg("支付成功");
        } else {
            return new ResultUtil<>().setErrorMsg("未支付");
        }
    }

    /**
     * 修改领取点优惠信息
     *
     * @param detail
     * @param claimPointId
     * @return
     */
    @RequestMapping(value = "detail", method = RequestMethod.POST)
    public Result<Object> detail(String detail, Long claimPointId) {
        ClaimPoint claimPoint = claimPointService.find(claimPointId);
        if (detail == null || "".equals(detail)) {
            return new ResultUtil<>().setData(claimPoint.getDetail());
        }

        if (!claimPoint.getWechatUserId().equals(getUserId(request))) {
            return new ResultUtil<>().setErrorMsg("修改失败请重试");
        }
        claimPoint.setDetail(detail);
        claimPointService.save(claimPoint);
        return new ResultUtil<>().setSuccessMsg("修改成功");
    }

    /**
     * 查看用户领取点
     *
     * @return
     */
    @RequestMapping(value = "get", method = RequestMethod.GET)
    public Result<Object> get() {
        return new ResultUtil<>().setData(claimPointService.findByWechatUserIdAndState(getUserId(request), 1));
    }

    /**
     * 合伙人领取点
     *
     * @return
     */
    @RequestMapping(value = "partner", method = RequestMethod.GET)
    public Result<Object> partner() {
        return new ResultUtil<>().setData(claimPointService.listByRecId(getUserId(request)));
    }

    /**
     * 附近领取点
     *
     * @param lat
     * @param lon
     * @return
     */
    @RequestMapping(value = "nearby", method = RequestMethod.POST)
    public Result<Object> nearby(double lat, double lon) {
        double[] coords = MapUtil.getAround(lat, lon, 25000);
        BigDecimal minLat = new BigDecimal(coords[0]);
        BigDecimal minLon = new BigDecimal(coords[1]);
        BigDecimal maxLat = new BigDecimal(coords[2]);
        BigDecimal maxLon = new BigDecimal(coords[3]);

        List<ClaimPoint> claimPoints = claimPointService.listByStateAndLatitudeBetweenAndLongitudeBetween(1, minLat, maxLat, minLon, maxLon);

        int size = claimPoints.size();
        List<ClaimPointVo> claimPointVos = new ArrayList<>(size);
        for (ClaimPoint claimPoint : claimPoints) {
            double distance = MapUtil.getDistance(lat, lon, claimPoint.getLatitude().doubleValue(), claimPoint.getLongitude().doubleValue()) / 1000;
            ClaimPointVo claimPointVo = new ClaimPointVo(claimPoint, String.format("%.1f", distance));
            claimPointVos.add(claimPointVo);
        }
        claimPointVos.sort((ClaimPointVo claimPointVo1, ClaimPointVo claimPointVo2) -> {
            float d1 = Float.valueOf(claimPointVo1.getDistance());
            float d2 = Float.valueOf(claimPointVo2.getDistance());
            if (d1 > d2) {
                return 1;
            } else if (d1 < d2) {
                return -1;
            } else {
                return 0;
            }
        });
        return new ResultUtil<>().setData(claimPointVos);
    }

    private void sendTemplateMessage(String formId, String openId, Integer rewardAmount) throws WxErrorException {
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setFormId(formId);
        wxMaTemplateMessage.setToUser(openId);
        wxMaTemplateMessage.setTemplateId("PozVhy5ryu2RM_MnJwJslAxh9phokxGI8PTW03c1KzI");
        wxMaTemplateMessage.setEmphasisKeyword("keyword3.DATA");
        wxMaTemplateMessage.setPage("pages/Index");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List<WxMaTemplateData> list = new ArrayList<>();
        list.add(new WxMaTemplateData("keyword1", "推荐领取点"));
        list.add(new WxMaTemplateData("keyword2", sdf.format(new Date())));
        list.add(new WxMaTemplateData("keyword3", String.format("%.2f", rewardAmount / 100.0) + "元"));
        wxMaTemplateMessage.setData(list);
        wxMaMsgService.sendTemplateMsg(wxMaTemplateMessage);
    }
}
