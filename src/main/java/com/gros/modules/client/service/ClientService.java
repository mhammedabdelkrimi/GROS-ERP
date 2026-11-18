package com.gros.modules.client.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.NotFoundException;
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

@Service @RequiredArgsConstructor
public class ClientService {
    private final ClientRepository repo;
    private final ClientMapper mapper;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public Page<ClientResponse> lister(String q, Pageable pageable) {
        return repo.findByActifTrueAndRaisonSocialeContainingIgnoreCase(q == null ? "" : q, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public ClientResponse creer(ClientRequest r) {
        Client c = repo.save(mapper.toEntity(r));
        audit.tracer("CREATION", "CLIENT", c.getId());
        return mapper.toResponse(c);
    }

    @Transactional
    public ClientResponse modifier(Long id, ClientRequest r) {
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
}