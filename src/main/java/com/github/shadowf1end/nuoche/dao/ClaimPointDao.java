package com.github.shadowf1end.nuoche.dao;

import com.github.shadowf1end.nuoche.base.BaseDao;
import com.github.shadowf1end.nuoche.entity.ClaimPoint;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author su
 */
public interface ClaimPointDao extends BaseDao<ClaimPoint, Long> {
    List<ClaimPoint> findByWechatUserIdAndState(Long wechatUserId, Integer state);

    List<ClaimPoint> findAllByStateAndLatitudeBetweenAndLongitudeBetween(Integer state, BigDecimal latitude, BigDecimal latitude2, BigDecimal longitude, BigDecimal longitude2);

//    List<ClaimPoint> findAllByPartnerId(Integer partnerId);

    List<ClaimPoint> findAllByRecId(Long recId);
}
