package com.github.shadowf1end.nuoche.service.impl;

import com.github.shadowf1end.nuoche.dao.VoiceRecordDao;
import com.github.shadowf1end.nuoche.entity.VoiceRecord;
import com.github.shadowf1end.nuoche.service.VoiceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author su
 */
@Service("voiceRecordService")
public class VoiceRecordServiceImpl implements VoiceRecordService {

    private final VoiceRecordDao voiceRecordDao;

    @Autowired
    public VoiceRecordServiceImpl(VoiceRecordDao voiceRecordDao) {
        this.voiceRecordDao = voiceRecordDao;
    }

    @Override
    public VoiceRecord save(VoiceRecord voiceRecord) {
        return voiceRecordDao.save(voiceRecord);
    }
}