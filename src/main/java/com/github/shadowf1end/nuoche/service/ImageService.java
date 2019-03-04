package com.github.shadowf1end.nuoche.service;

import com.github.shadowf1end.nuoche.entity.Image;

import java.util.List;

/**
 * @author su
 */
public interface ImageService {
    List<Image> listByType(String type);
    Image find(Integer id);
}
