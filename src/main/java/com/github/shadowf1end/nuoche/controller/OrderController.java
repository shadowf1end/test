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
import com.github.shadowf1end.nuoche.common.util.RedisUtil;
import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import com.github.shadowf1end.nuoche.entity.Order;
import com.github.shadowf1end.nuoche.entity.WalletRecord;
import com.github.shadowf1end.nuoche.entity.WechatUser;
import com.github.shadowf1end.nuoche.service.ClaimPointService;
import com.github.shadowf1end.nuoche.service.OrderService;
import com.github.shadowf1end.nuoche.service.WalletRecordService;
import com.github.shadowf1end.nuoche.service.WechatUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author su
 */
@RestController
@RequestMapping("order")
public class OrderController extends BaseController {

    private final OrderService orderService;
    private final HttpServletRequest request;
    private final WechatUserService wechatUserService;
    private final WxPayService wxPayService;
    private final WalletRecordService walletRecordService;
    private final WxMaMsgService wxMaMsgService;
    private final ClaimPointService claimPointService;
    private final RedisUtil redisUtil;

    @Autowired
    public OrderController(OrderService orderService, HttpServletRequest request, WechatUserService wechatUserService, WxPayService wxPayService, WalletRecordService walletRecordService, WxMaMsgService wxMaMsgService, RedisUtil redisUtil, ClaimPointService claimPointService) {
        this.orderService = orderService;
        this.request = request;
        this.wechatUserService = wechatUserService;
        this.wxPayService = wxPayService;
        this.walletRecordService = walletRecordService;
        this.wxMaMsgService = wxMaMsgService;
        this.redisUtil = redisUtil;
        this.claimPointService = claimPointService;
    }

    /**
     * 个人订单列表
     *
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Result<Object> list() {
        return new ResultUtil<>().setData(orderService.listByWechatUserIdAndState(getUserId(request), 1));
    }

    /**
     * 购买挪车贴
     *
     * @param order
     * @return
     * @throws WxPayException
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "purchase", method = RequestMethod.POST)
    public Result<Object> purchase(Order order) throws WxPayException {
        if (order.getAmount() <= 0) {
            return new ResultUtil<>().setErrorMsg("请选择购买数量");
        }
        if (order.getClaimPointId() != null) {
            try {
                claimPointService.find(order.getClaimPointId());
            } catch (Exception e) {
                return new ResultUtil<>().setErrorMsg("领取点编号错误");
            }
        }
        WechatUser wechatUser = wechatUserService.find(getUserId(request));

        order.setWechatUserId(wechatUser.getId());
        orderService.save(order);

        String outTradeNo = order.getId().toString();
        String body = "挪车贴";
        String notifyUrl = "https://127.0.0.1";
        String openId = wechatUser.getOpenId();
        String ip = IpInfoUtil.getIpAddr(request);
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(body);

        Integer price;
        if (order.getAmount() >= 200 && order.getClaimPointId() != null && ("claim".equals(wechatUser.getRole()) || "all".equals(wechatUser.getRole()))) {
            if (redisUtil.hasKey("claim_point_price")) {
                price = Integer.valueOf(redisUtil.get("claim_point_price"));
            } else {
                price = 490;
            }
        } else {
            if (redisUtil.hasKey("user_price")) {
                price = Integer.valueOf(redisUtil.get("user_price"));
            } else {
                price = 2880;
            }
            order.setClaimPointId(null);
            order.setRecId(null);
        }
//        orderRequest.setTotalFee(1);
        order.setPrice(order.getAmount() * price);
        orderRequest.setTotalFee(order.getAmount() * price);

        orderRequest.setOpenid(openId);
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setNotifyUrl(notifyUrl);
        orderRequest.setTradeType("JSAPI");
        orderRequest.setOutTradeNo(outTradeNo);
        WxPayMpOrderResult result = wxPayService.createOrder(orderRequest);

        Map<String, Object> map = new HashMap<String, Object>(2) {
            {
                put("pay", result);
                put("no", order.getId().toString());
            }
        };
        return new ResultUtil<>().setData(map);
    }

    /**
     * 购买支付查询
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
            Order order = orderService.find(id);
            order.setState(1);
            orderService.save(order);
            try {
                if (order.getRecId() != null) {
                    if (!order.getRecId().equals(order.getWechatUserId())) {
                        WechatUser purUser = wechatUserService.find(order.getWechatUserId());
                        WechatUser recUser = wechatUserService.find(order.getRecId());
                        WalletRecord walletRecord = new WalletRecord();
                        walletRecord.setPreWallet(recUser.getWallet());
                        walletRecord.setVerifyFlag(1);
                        walletRecord.setType(2);
                        walletRecord.setWechatUserId(recUser.getId());
                        walletRecord.setOrderId(order.getId());
                        Integer rewardPercentage;
                        if (redisUtil.hasKey("reward_percentage")) {
                            rewardPercentage = Integer.valueOf(redisUtil.get("reward_percentage"));
                        } else {
                            rewardPercentage = 30;
                        }

                        if ("claim".equals(purUser.getRole()) || "all".equals(purUser.getRole())) {
                            walletRecord.setDes("领取点" + order.getClaimPointId() + "回购" + order.getAmount() + "张挪车贴");
                            if ("partner".equals(recUser.getRole()) || "all".equals(recUser.getRole())) {
                                recUser.setWallet(recUser.getWallet() + (int) (order.getPrice() * rewardPercentage / 100.0));
                                walletRecord.setAmount((int) (order.getPrice() * rewardPercentage / 100.0));
                                wechatUserService.save(recUser);
                                walletRecord.setCurWallet(recUser.getWallet());
                                walletRecordService.save(walletRecord);
                            }
                            sendTemplateMessage(wechatUserService.getFormId(recUser.getId()), recUser.getOpenId(), String.valueOf(walletRecord.getAmount() * 0.01));
                        }
//                    else {
//                        walletRecord.setDes("推荐" + purUser.getNickName() + "购买" + order.getAmount() + "张挪车贴");
//                        recUser.setWallet(recUser.getWallet() + order.getAmount() * 3000);
//                        walletRecord.setAmount(order.getAmount() * 3000);
//                    }
                    }
                }
            } catch (Exception e) {
                return new ResultUtil<>().setSuccessMsg("支付成功");
            }
            return new ResultUtil<>().setSuccessMsg("支付成功");
        } else {
            return new ResultUtil<>().setErrorMsg("未支付");
        }
    }

    private void sendTemplateMessage(String formId, String openId, String amount) throws WxErrorException {
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setFormId(formId);
        wxMaTemplateMessage.setToUser(openId);
        wxMaTemplateMessage.setTemplateId("PozVhy5ryu2RM_MnJwJslAxh9phokxGI8PTW03c1KzI");
        wxMaTemplateMessage.setEmphasisKeyword("keyword3.DATA");
        wxMaTemplateMessage.setPage("pages/Index");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List<WxMaTemplateData> list = new ArrayList<>();
        list.add(new WxMaTemplateData("keyword1", "领取点回购"));
        list.add(new WxMaTemplateData("keyword2", sdf.format(new Date())));
        list.add(new WxMaTemplateData("keyword3", amount + "元"));
        wxMaTemplateMessage.setData(list);
        wxMaMsgService.sendTemplateMsg(wxMaTemplateMessage);
    }
}
