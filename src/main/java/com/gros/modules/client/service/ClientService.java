package com.gros.modules.client.service;

import com.gros.common.SecurityUtils;
import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.parametre.ParametreService;
import com.gros.modules.auth.entity.Role;
import com.gros.modules.auth.entity.Utilisateur;
import com.gros.modules.client.dto.ClientRequest;
import com.gros.modules.client.dto.ClientResponse;
import com.gros.modules.client.entity.Client;
import com.gros.modules.client.mapper.ClientMapper;
import com.gros.modules.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service @RequiredArgsConstructor
public class ClientService {
    private final ClientRepository repo;
    private final ClientMapper mapper;
    private final ParametreService parametres;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public Page<ClientResponse> lister(String q, Pageable pageable) {
        return repo.findByActifTrueAndRaisonSocialeContainingIgnoreCase(q == null ? "" : q, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public ClientResponse creer(ClientRequest r) {
        verifierRemise(r);
        Client c = repo.save(mapper.toEntity(r));
        audit.tracer("CREATION", "CLIENT", c.getId());
        return mapper.toResponse(c);
    }

    @Transactional
    public ClientResponse modifier(Long id, ClientRequest r) {
        verifierRemise(r);
        Client c = trouver(id);
        mapper.maj(r, c);
        audit.tracer("MODIFICATION", "CLIENT", id);
        return mapper.toResponse(c);
    }

    @Transactional
    public void desactiver(Long id) {
        trouver(id).desactiver();
        audit.tracer("DESACTIVATION", "CLIENT", id);
    }

    @Transactional
    public void bloquer(Long id) {
        trouver(id).bloquer();
        audit.tracer("BLOCAGE", "CLIENT", id);
    }

    @Transactional
    public void debloquer(Long id) {
        trouver(id).debloquer();
        audit.tracer("DEBLOCAGE", "CLIENT", id);
    }

    public Client trouver(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Client introuvable : " + id));
    }

    /** Une remise habituelle supérieure au plafond doit être accordée par la direction. */
    private void verifierRemise(ClientRequest r) {
        if (r.remiseHabituelle() == null) return;
        BigDecimal plafond = parametres.decimal("PLAFOND_REMISE_POURCENT");
        Utilisateur u = SecurityUtils.utilisateurCourant();
        boolean direction = u != null && (u.getRole() == Role.DIRECTEUR || u.getRole() == Role.ADMINISTRATEUR);
        if (!direction && r.remiseHabituelle().compareTo(plafond) > 0)
            throw new BusinessException("Remise supérieure au plafond (" + plafond
                    + " %) : validation de la direction requise");
    }
}