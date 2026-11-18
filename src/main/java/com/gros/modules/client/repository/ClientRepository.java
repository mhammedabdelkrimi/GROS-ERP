package com.gros.modules.client.repository;

import com.gros.modules.client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findByActifTrueAndRaisonSocialeContainingIgnoreCase(String q, Pageable p);
}