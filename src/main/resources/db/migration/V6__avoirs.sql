ALTER TABLE facture_client ADD (montant_avoir NUMBER(15,2) DEFAULT 0 NOT NULL);
ALTER TABLE facture_client ADD CONSTRAINT ck_avoir CHECK (montant_avoir >= 0 AND montant_paye + montant_avoir <= montant_ttc);
ALTER TABLE avoir ADD (cree_par NUMBER REFERENCES utilisateur(id));