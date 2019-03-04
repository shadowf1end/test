package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.TemplateDao;
import com.github.shadowf1end.nuoche.entity.Template;
import com.github.shadowf1end.nuoche.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author su
 */
@Service("templateService")
public class TemplateServiceImpl implements TemplateService {

    private final TemplateDao templateDao;

    @Autowired
    public TemplateServiceImpl(TemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    @Override
    public List<Template> list() {
        return templateDao.findAll();
    }
}
