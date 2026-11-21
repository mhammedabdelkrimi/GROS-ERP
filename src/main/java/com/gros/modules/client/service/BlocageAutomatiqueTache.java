package com.gros.modules.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component @RequiredArgsConstructor @Slf4j
public class BlocageAutomatiqueTache {
    private final BlocageAutomatiqueService service;

    @Scheduled(cron = "${app.blocage-auto.cron:0 0 2 * * *}")
    public void executer() {
        List<Long> bloques = service.executer();
        if (!bloques.isEmpty()) log.info("Blocage automatique : {} client(s) bloqué(s) {}", bloques.size(), bloques);
    }
}