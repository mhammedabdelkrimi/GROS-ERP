ALTER TABLE facture_client ADD (montant_rembourse NUMBER(15,2) DEFAULT 0 NOT NULL);
ALTER TABLE facture_client ADD CONSTRAINT ck_rembourse CHECK (montant_rembourse >= 0 AND montant_rembourse <= montant_paye);

DELETE FROM remboursement;
ALTER TABLE remboursement ADD (facture_id NUMBER NOT NULL REFERENCES facture_client(id));