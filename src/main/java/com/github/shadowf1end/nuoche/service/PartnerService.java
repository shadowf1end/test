package com.github.shadowf1end.nuoche.service;

import com.github.shadowf1end.nuoche.entity.Partner;

public interface PartnerService {
    Partner save(Partner partner);

    Partner findByWechatUserId(Long wechatUserId);

    Partner find(Integer id);
}
