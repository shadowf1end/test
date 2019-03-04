package com.github.shadowf1end.nuoche.controller;

import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import com.github.shadowf1end.nuoche.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author su
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<Object> list(String type) {
        return new ResultUtil<>().setData(imageService.listByType(type));
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Result<Object> get(Integer id) {
        return new ResultUtil<>().setData(imageService.find(id));
    }
}
