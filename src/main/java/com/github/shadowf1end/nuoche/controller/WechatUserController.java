package com.github.shadowf1end.nuoche.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.github.shadowf1end.nuoche.base.BaseController;
import com.github.shadowf1end.nuoche.common.util.JwtUtil;
import com.github.shadowf1end.nuoche.common.util.RedisUtil;
import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import com.github.shadowf1end.nuoche.entity.WechatUser;
import com.github.shadowf1end.nuoche.service.WechatUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author su
 */
@RestController
@RequestMapping("wechatUser")
public class WechatUserController extends BaseController {

    private final WechatUserService wechatUserService;
    private final WxMaService wxMaService;
    private final HttpServletRequest request;
    private final RedisUtil redisUtil;

    @Autowired
    public WechatUserController(WechatUserService wechatUserService, WxMaService wxMaService, HttpServletRequest request, RedisUtil redisUtil) {
        this.wechatUserService = wechatUserService;
        this.wxMaService = wxMaService;
        this.request = request;
        this.redisUtil = redisUtil;
    }

    /**
     * 钱包余额
     * @return
     */
    @RequestMapping(value = "wallet", method = RequestMethod.GET)
    public Result<Object> wallet() {
        Map<String, String> map = new HashMap<String, String>(2){
            {
                put("wallet", wechatUserService.find(getUserId(request)).getWallet().toString());
                put("ann", redisUtil.get("announcement"));
            }
        };
        return new ResultUtil<>().setData(map);
    }

    /**
     * 获取用户角色
     * @return
     */
    @RequestMapping(value = "role", method = RequestMethod.GET)
    public Result<Object> role() {
        return new ResultUtil<>().setData(wechatUserService.find(getUserId(request)).getRole());
    }

    @RequestMapping(value = "recommend", method = RequestMethod.GET)
    public Result<Object> recommend() {
        return new ResultUtil<>().setData(getUserId(request).toString());
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Result<Object> login(String code, String nickName, String avatarUrl) throws WxErrorException {
        WxMaJscode2SessionResult result = wxMaService.jsCode2SessionInfo(code);
        String openId = result.getOpenid();
        String sessionKey = result.getSessionKey();
        WechatUser wechatUser = wechatUserService.findByOpenId(openId);
        if (wechatUser == null) {
            wechatUser = new WechatUser();
        }

        wechatUser.setOpenId(openId);
        wechatUser.setSessionKey(sessionKey);
        wechatUser.setNickName(nickName);
        wechatUser.setAvatarUrl(avatarUrl);

        if (wechatUserService.save(wechatUser) == null) {
            return new ResultUtil<>().setErrorMsg("系统异常，请稍后再试");
        } else {
            return new ResultUtil<>().setData(JwtUtil.generateJwt(wechatUser.getId()));
        }
    }

    /**
     * 获取formId
     *
     * @param formId
     * @return
     */
    @RequestMapping(value = "formId", method = RequestMethod.POST)
    public Result<Object> saveFormId(String formId) {
        if (wechatUserService.cacheFormId(getUserId(request), formId)) {
//            System.out.println("success");
            return new ResultUtil<>().setSuccessMsg("success");
        } else {
//            System.out.println("fail");
            return new ResultUtil<>().setErrorMsg("fail");
        }
    }
}
