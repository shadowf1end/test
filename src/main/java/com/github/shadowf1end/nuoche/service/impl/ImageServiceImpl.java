package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.ImageDao;
import com.github.shadowf1end.nuoche.entity.Image;
import com.github.shadowf1end.nuoche.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author su
 */
@Service("imageService")
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Override
    public List<Image> listByType(String type) {
        return imageDao.findAllByType(type);
    }

    @Override
    public Image find(Integer id) {
        return imageDao.getOne(id);
    }
}