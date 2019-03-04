package com.github.shadowf1end.nuoche.common.singleton;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author su
 */
public class SnowFlakeSingleton {

    private SnowFlakeSingleton() {
    }

    private static class InnerClass {
        private static final Snowflake INSTANCE = IdUtil.createSnowflake(0, 0);
    }

    public static Snowflake getInstance() {
        return InnerClass.INSTANCE;
    }
}
