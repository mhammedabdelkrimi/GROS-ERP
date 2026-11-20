package com.gros.modules.facturation.service;
import java.time.LocalDate;
import java.util.List;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.facturation.dto.FactureResponse;
import com.gros.modules.facturation.repository.FactureClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service @RequiredArgsConstructor
public class FactureService {
    private final FactureClientRepository factures;

    @Transactional(readOnly = true)
    public FactureResponse consulter(Long id) {
        return factures.findById(id).map(FactureResponse::de)
                .orElseThrow(() -> new NotFoundException("Facture introuvable : " + id));
    }
    @Transactional(readOnly = true)
    public List<FactureResponse> impayees() {
        return factures.echues(List.of(StatutFacture.EMISE, StatutFacture.PARTIELLEMENT_PAYEE), LocalDate.now())
                .stream().map(FactureResponse::de).toList();
    }
    @Transactional(readOnly = true)
    public Page<FactureResponse> lister(StatutFacture statut, Pageable pageable) {
        var page = (statut == null) ? factures.findAll(pageable) : factures.findByStatut(statut, pageable);
        return page.map(FactureResponse::de);
    }
}