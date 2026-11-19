package com.gros.common.document;

import com.gros.common.audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class ServiceDocument<T> {
    @Autowired protected AuditService audit;

    @Transactional
    public void valider(Long id) {
        T doc = charger(id);
        verifierRegles(doc);      // 1. vérifier
        appliquerEffets(doc);     // 2. appliquer
        audit.tracer("VALIDATION", nomEntite(), idDe(doc));   // 3. tracer
    }

    protected abstract T charger(Long id);
    protected abstract void verifierRegles(T doc);
    protected abstract void appliquerEffets(T doc);
    protected abstract String nomEntite();
    protected abstract Long idDe(T doc);
}