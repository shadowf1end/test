package com.github.shadowf1end.nuoche.service;

import com.github.shadowf1end.nuoche.entity.Bundling;

import java.util.List;

/**
 * @author su
 */
public interface BundlingService {
    Bundling save(Bundling bundling);

    Bundling find(Long id);

    List<Bundling> listByWechatUserId(Long wechatUserId);

    List<Bundling> listByClaimPointId(Long claimPointId, Integer page, Integer size);

    Integer countByClaimPointIdAndWechatUserIdIsNotNull(Long claimPointId);

    Integer countByClaimPointId(Long claimPointId);

    List<Long> listWechatUserIdByClaimPointId(Long claimPointId);
}
