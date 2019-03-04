package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.ClaimPointDao;
import com.github.shadowf1end.nuoche.entity.ClaimPoint;
import com.github.shadowf1end.nuoche.service.ClaimPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author su
 */
@Service("claimPointService")
public class ClaimPointServiceImpl implements ClaimPointService {

    private final ClaimPointDao claimPointDao;

    @Autowired
    public ClaimPointServiceImpl(ClaimPointDao claimPointDao) {
        this.claimPointDao = claimPointDao;
    }

    @Override
    public ClaimPoint find(Long id) {
        return claimPointDao.getOne(id);
    }

    @Override
    public ClaimPoint save(ClaimPoint claimPoint) {
        return claimPointDao.saveAndFlush(claimPoint);
    }

    @Override
    public List<ClaimPoint> findByWechatUserIdAndState(Long wechatUserId, Integer state) {
        return claimPointDao.findByWechatUserIdAndState(wechatUserId, state);
    }

    @Override
    public List<ClaimPoint> listByStateAndLatitudeBetweenAndLongitudeBetween(Integer state, BigDecimal latitude, BigDecimal latitude2, BigDecimal longitude, BigDecimal longitude2) {
        return claimPointDao.findAllByStateAndLatitudeBetweenAndLongitudeBetween(state, latitude, latitude2, longitude, longitude2);
    }

//    @Override
//    public List<ClaimPoint> listByPartnerId(Integer partnerId) {
//        return claimPointDao.findAllByPartnerId(partnerId);
//    }

    @Override
    public List<ClaimPoint> listByRecId(Long recId) {
        return claimPointDao.findAllByRecId(recId);
    }
}
