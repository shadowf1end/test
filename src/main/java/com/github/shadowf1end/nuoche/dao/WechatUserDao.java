package com.github.shadowf1end.nuoche.dao;

import com.github.shadowf1end.nuoche.base.BaseDao;
import com.github.shadowf1end.nuoche.entity.WechatUser;

/**
 * @author su
 */
public interface WechatUserDao extends BaseDao<WechatUser, Long> {
    WechatUser findByOpenId(String openId);
}
