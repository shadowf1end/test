package com.github.shadowf1end.nuoche.service;

import com.github.shadowf1end.nuoche.entity.WechatUser;

/**
 * @author su
 */
public interface WechatUserService {
    WechatUser save(WechatUser wechatUser);

    WechatUser findByOpenId(String openId);

    WechatUser find(Long id);

    boolean cacheFormId(Long wechatUserId, String formId);

    String getFormId(Long wechatUserId);
}
