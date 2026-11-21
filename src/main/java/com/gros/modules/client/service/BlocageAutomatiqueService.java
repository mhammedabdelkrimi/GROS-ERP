package com.gros.modules.client.service;

import com.gros.common.audit.AuditService;
import com.gros.common.parametre.ParametreService;
import com.gros.modules.client.entity.Client;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.repository.FactureClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Section 2.5 : un client dont une facture reste impayée trop longtemps ne peut plus passer de commande. */
@Service @RequiredArgsConstructor
public class BlocageAutomatiqueService {
    private static final List<StatutFacture> IMPAYEES =
            List.of(StatutFacture.EMISE, StatutFacture.PARTIELLEMENT_PAYEE);

    private final FactureClientRepository factures;
    private final ClientRepository clients;
    private final ParametreService parametres;
    private final AuditService audit;

    /** Renvoie les ids des clients bloqués par cette exécution. */
    @Transactional
    public List<Long> executer() {
        int joursRetard = parametres.decimal("JOURS_RETARD_BLOCAGE").intValue();
        int joursGrace = parametres.decimal("JOURS_GRACE_DEBLOCAGE").intValue();
        LocalDate aujourdhui = LocalDate.now();

        Set<Long> enRetard = factures.echues(IMPAYEES, aujourdhui.minusDays(joursRetard)).stream()
                .filter(f -> f.soldeRestant().signum() > 0)
                .map(FactureClient::getClientId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Long> bloques = new ArrayList<>();
        for (Long id : enRetard) {
            clients.findById(id)
                    .filter(Client::isActif)
                    .filter(c -> !c.isBloque())
                    .filter(c -> !c.estEnGrace(joursGrace, aujourdhui))
                    .ifPresent(c -> {
                        c.bloquer();
                        audit.tracer("BLOCAGE_AUTO", "CLIENT", c.getId());
                        bloques.add(c.getId());
                    });
        }
        return bloques;
    }
}