package com.github.shadowf1end.nuoche.common.singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author su
 */
public class ObjectMapperSingleton {

    private ObjectMapperSingleton() {
    }

    private static class InnerClass {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }

    public static ObjectMapper getInstance() {
        return InnerClass.INSTANCE;
    }
}
