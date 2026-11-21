ALTER TABLE inventaire ADD (
  numero VARCHAR2(30),
  cree_par NUMBER REFERENCES utilisateur(id),
  cloture_par NUMBER REFERENCES utilisateur(id),
  date_cloture TIMESTAMP
);
ALTER TABLE inventaire ADD CONSTRAINT uk_inventaire_numero UNIQUE (numero);

ALTER TABLE ligne_inventaire MODIFY (quantite_comptee NULL);
ALTER TABLE ligne_inventaire ADD CONSTRAINT uk_ligne_inv UNIQUE (inventaire_id, produit_id);
ALTER TABLE ligne_inventaire ADD CONSTRAINT ck_comptee CHECK (quantite_comptee IS NULL OR quantite_comptee >= 0);