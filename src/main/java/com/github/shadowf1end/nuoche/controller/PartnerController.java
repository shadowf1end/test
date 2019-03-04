package com.github.shadowf1end.nuoche.controller;

import com.github.shadowf1end.nuoche.base.BaseController;
import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import com.github.shadowf1end.nuoche.entity.Partner;
import com.github.shadowf1end.nuoche.service.PartnerService;
import com.github.shadowf1end.nuoche.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author su
 */
@RestController
@RequestMapping("/partner")
public class PartnerController extends BaseController {

    private final HttpServletRequest request;
    private final PartnerService partnerService;
    private final WechatUserService wechatUserService;

    @Autowired
    public PartnerController(HttpServletRequest request, PartnerService partnerService, WechatUserService wechatUserService) {
        this.request = request;
        this.partnerService = partnerService;
        this.wechatUserService = wechatUserService;
    }

    /**
     * 申请合伙人
     * @param partner
     * @return
     */
    @RequestMapping(value = "apply", method = RequestMethod.POST)
    public Result<Object> apply(Partner partner) {
        String role = wechatUserService.find(getUserId(request)).getRole();
        if ("partner".equals(role) || "all".equals(role)) {
            return new ResultUtil<>().setErrorMsg("已是合伙人");
        }
        if (partnerService.findByWechatUserId(getUserId(request)) != null) {
            return new ResultUtil<>().setErrorMsg("已申请过合伙人");
        }
        partner.setWechatUserId(getUserId(request));
        partnerService.save(partner);
        return new ResultUtil<>().setSuccessMsg("已提交审核");
    }
}
