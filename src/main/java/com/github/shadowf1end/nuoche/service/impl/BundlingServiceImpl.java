package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.BundlingDao;
import com.github.shadowf1end.nuoche.entity.Bundling;
import com.github.shadowf1end.nuoche.service.BundlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author su
 */
@Service("bundlingService")
public class BundlingServiceImpl implements BundlingService {

    private final BundlingDao bundlingDao;

    @Autowired
    public BundlingServiceImpl(BundlingDao bundlingDao) {
        this.bundlingDao = bundlingDao;
    }

    @Override
    public Bundling save(Bundling bundling) {
        return bundlingDao.saveAndFlush(bundling);
    }

    @Override
    public Bundling find(Long id) {
        return bundlingDao.getOne(id);
    }

    @Override
    public List<Bundling> listByWechatUserId(Long wechatUserId) {
        return bundlingDao.findAllByWechatUserId(wechatUserId);
    }

    @Override
    public List<Bundling> listByClaimPointId(Long claimPointId, Integer page, Integer size) {
        return bundlingDao.findAllByClaimPointIdAndWechatUserIdIsNotNull(claimPointId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime", "createTime")));
    }

    @Override
    public Integer countByClaimPointIdAndWechatUserIdIsNotNull(Long claimPointId) {
        return bundlingDao.countByClaimPointIdAndWechatUserIdIsNotNull(claimPointId);
    }

    @Override
    public Integer countByClaimPointId(Long claimPointId) {
        return bundlingDao.countByClaimPointId(claimPointId);
    }

    @Override
    public List<Long> listWechatUserIdByClaimPointId(Long claimPointId) {
        return bundlingDao.findWechatUserIdByClaimPointId(claimPointId);
    }
}
