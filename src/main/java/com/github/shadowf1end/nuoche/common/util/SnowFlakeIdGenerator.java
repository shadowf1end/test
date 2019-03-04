package com.github.shadowf1end.nuoche.common.util;

import com.github.shadowf1end.nuoche.common.singleton.SnowFlakeSingleton;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * @author su
 */
public class SnowFlakeIdGenerator implements IdentifierGenerator {
    public SnowFlakeIdGenerator() {
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return SnowFlakeSingleton.getInstance().nextId();
    }
}

