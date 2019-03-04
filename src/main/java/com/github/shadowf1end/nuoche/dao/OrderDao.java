package com.github.shadowf1end.nuoche.dao;

import com.github.shadowf1end.nuoche.base.BaseDao;
import com.github.shadowf1end.nuoche.entity.Order;

import java.util.List;

/**
 * @author su
 */
public interface OrderDao extends BaseDao<Order, Long> {
    List<Order> findAllByWechatUserIdAndState(Long wechatUserId, Integer state);
}
