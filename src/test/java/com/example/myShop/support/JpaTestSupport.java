package com.example.myShop.support;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class JpaTestSupport {

    @PersistenceContext
    protected EntityManager em;

    protected void flushAndClear() {
        em.flush();
        em.clear();
    }
}
