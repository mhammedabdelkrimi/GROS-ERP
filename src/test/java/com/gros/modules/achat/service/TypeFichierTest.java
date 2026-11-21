package com.gros.modules.achat.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeFichierTest {

    @Test void reconnaitUnPdf() {
        assertEquals("application/pdf",
                TypeFichier.detecter("%PDF-1.4 test".getBytes(StandardCharsets.US_ASCII)).orElseThrow());
    }

    @Test void reconnaitPngEtJpeg() {
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A};
        byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00};
        assertEquals("image/png", TypeFichier.detecter(png).orElseThrow());
        assertEquals("image/jpeg", TypeFichier.detecter(jpeg).orElseThrow());
    }

    @Test void refuseUnFichierQuelconque() {
        assertTrue(TypeFichier.detecter("MZ executable ou texte".getBytes(StandardCharsets.US_ASCII)).isEmpty());
        assertTrue(TypeFichier.detecter(new byte[0]).isEmpty());
    }

    @Test void nettoieLeNomDuFichier() {
        assertEquals("evil_script_.pdf", TypeFichier.nettoyerNom("..\\..\\evil<script>.pdf"));
        assertEquals("piece", TypeFichier.nettoyerNom(null));
    }
}