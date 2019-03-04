package com.github.shadowf1end.nuoche.dao;

import com.github.shadowf1end.nuoche.base.BaseDao;
import com.github.shadowf1end.nuoche.entity.Image;

import java.util.List;

/**
 * @author su
 */
public interface ImageDao extends BaseDao<Image, Integer> {
    List<Image> findAllByType(String type);
}
