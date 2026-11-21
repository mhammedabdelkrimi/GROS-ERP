ALTER TABLE client ADD (
  remise_habituelle NUMBER(5,2) DEFAULT 0 NOT NULL,
  date_deblocage DATE
);
ALTER TABLE client ADD CONSTRAINT ck_client_remise CHECK (remise_habituelle BETWEEN 0 AND 100);

INSERT INTO parametre (cle, valeur) VALUES ('JOURS_RETARD_BLOCAGE', '30');
INSERT INTO parametre (cle, valeur) VALUES ('JOURS_GRACE_DEBLOCAGE', '7');