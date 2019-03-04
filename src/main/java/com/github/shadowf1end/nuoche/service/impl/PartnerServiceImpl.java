package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.PartnerDao;
import com.github.shadowf1end.nuoche.entity.Partner;
import com.github.shadowf1end.nuoche.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("partnerService")
public class PartnerServiceImpl implements PartnerService {

    private final PartnerDao partnerDao;

    @Autowired
    public PartnerServiceImpl(PartnerDao partnerDao) {
        this.partnerDao = partnerDao;
    }

    @Override
    public Partner save(Partner partner) {
        return partnerDao.saveAndFlush(partner);
    }

    @Override
    public Partner findByWechatUserId(Long wechatUserId) {
        return partnerDao.findByWechatUserId(wechatUserId);
    }

    @Override
    public Partner find(Integer id) {
        return partnerDao.getOne(id);
    }
}
