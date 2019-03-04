package com.github.shadowf1end.nuoche.service;

import com.github.shadowf1end.nuoche.entity.ClaimPoint;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author su
 */
public interface ClaimPointService {
    ClaimPoint find(Long id);

    ClaimPoint save(ClaimPoint claimPoint);

    List<ClaimPoint> findByWechatUserIdAndState(Long wechatUserId, Integer state);

    List<ClaimPoint> listByStateAndLatitudeBetweenAndLongitudeBetween(Integer state, BigDecimal latitude, BigDecimal latitude2, BigDecimal longitude, BigDecimal longitude2);

//    List<ClaimPoint> listByPartnerId(Integer partnerId);

    List<ClaimPoint> listByRecId(Long recId);
}
