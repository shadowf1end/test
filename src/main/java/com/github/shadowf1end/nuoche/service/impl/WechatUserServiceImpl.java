package com.github.shadowf1end.nuoche.service.impl;


import com.github.shadowf1end.nuoche.common.util.RedisUtil;
import com.github.shadowf1end.nuoche.dao.WechatUserDao;
import com.github.shadowf1end.nuoche.entity.WechatUser;
import com.github.shadowf1end.nuoche.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author su
 */
@Service("wechatUserService")
public class WechatUserServiceImpl implements WechatUserService {

    private final WechatUserDao wechatUserDao;
    private final RedisUtil redisUtil;

    @Autowired
    public WechatUserServiceImpl(WechatUserDao wechatUserDao, RedisUtil redisUtil) {
        this.wechatUserDao = wechatUserDao;
        this.redisUtil = redisUtil;
    }

    @Override
    public WechatUser save(WechatUser wechatUser) {
        return wechatUserDao.saveAndFlush(wechatUser);
    }

    @Override
    public WechatUser findByOpenId(String openId) {
        return wechatUserDao.findByOpenId(openId);
    }

    @Override
    public WechatUser find(Long id) {
        return wechatUserDao.getOne(id);
    }

    @Override
    public boolean cacheFormId(Long userId, String formId) {
        return redisUtil.zAdd("formId:" + userId, formId, System.currentTimeMillis());
    }

    @Override
    public String getFormId(Long userId) {
        String key = "formId:" + userId;
        redisUtil.zRemoveRangeByScore(key, 0, System.currentTimeMillis() - 604800000);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.zRangeWithScores(key, 0, -1);
        if (typedTuples.isEmpty()) {
            return null;
        }
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            long score = typedTuple.getScore().longValue();
            String value = typedTuple.getValue();
            redisUtil.zRemove(key, value);
            if (System.currentTimeMillis() - score < 604800000L) {
                return value;
            }
        }
        return null;
    }
}