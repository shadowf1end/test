package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.OrderDao;
import com.github.shadowf1end.nuoche.entity.Order;
import com.github.shadowf1end.nuoche.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author su
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order save(Order order) {
        return orderDao.saveAndFlush(order);
    }

    @Override
    public List<Order> listByWechatUserIdAndState(Long wechatUserId, Integer state) {
        return orderDao.findAllByWechatUserIdAndState(wechatUserId, state);
    }

    @Override
    public Order find(Long id) {
        return orderDao.getOne(id);
    }
}