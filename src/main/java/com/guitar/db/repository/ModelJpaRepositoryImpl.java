package com.guitar.db.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ModelJpaRepositoryImpl implements ModelJpaRepositoryCustom {

    private static final Log LOGGER = LogFactory.getLog(ModelJpaRepositoryImpl.class.getName());

    @Override
    public void aCustomMethod() {
        LOGGER.info("I'm a custom method.");
    }

}
