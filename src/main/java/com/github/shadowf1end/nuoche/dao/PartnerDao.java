package com.github.shadowf1end.nuoche.dao;

import com.github.shadowf1end.nuoche.base.BaseDao;
import com.github.shadowf1end.nuoche.entity.Partner;

/**
 * @author su
 */
public interface PartnerDao extends BaseDao<Partner, Integer> {
    Partner findByWechatUserId(Long wechatUserId);
}
