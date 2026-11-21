ALTER TABLE ligne_facture ADD (cout_unitaire NUMBER(15,2) DEFAULT 0 NOT NULL);

UPDATE ligne_facture lf
SET cout_unitaire = (SELECT p.prix_achat FROM produit p WHERE p.id = lf.produit_id);