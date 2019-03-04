package com.github.shadowf1end.nuoche.dao;

import com.github.shadowf1end.nuoche.base.BaseDao;
import com.github.shadowf1end.nuoche.entity.Bundling;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author su
 */
public interface BundlingDao extends BaseDao<Bundling, Long> {
    List<Bundling> findAllByWechatUserId(Long wechatUserId);

    List<Bundling> findAllByClaimPointIdAndWechatUserIdIsNotNull(Long claimPointId, Pageable pageable);

    Integer countByClaimPointIdAndWechatUserIdIsNotNull(Long claimPointId);

    Integer countByClaimPointId(Long claimPointId);

    @Query(value = "select wechatUserId from Bundling where claimPointId = :claimPointId group by wechatUserId having wechatUserId is not null")
    List<Long> findWechatUserIdByClaimPointId(@Param("claimPointId") Long claimPointId);
}
