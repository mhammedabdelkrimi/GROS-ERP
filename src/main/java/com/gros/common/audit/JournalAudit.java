package com.gros.common.audit;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "journal_audit")
@Getter
@Setter
@NoArgsConstructor
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long utilisateurId;
    private String action;
    private String entite;
    private Long entiteId;
    private LocalDateTime dateHeure = LocalDateTime.now();
}