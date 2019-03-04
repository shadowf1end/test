package com.github.shadowf1end.nuoche.service;

import com.github.shadowf1end.nuoche.entity.Order;

import java.util.List;

/**
 * @author su
 */
public interface OrderService {
    Order save(Order order);

    List<Order> listByWechatUserIdAndState(Long wechatUserId, Integer state);

    Order find(Long id);
}
