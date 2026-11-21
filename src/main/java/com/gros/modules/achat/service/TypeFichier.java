package com.gros.modules.achat.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

public final class TypeFichier {
    private TypeFichier() {}

    /** PDF, PNG ou JPEG, reconnus à leurs premiers octets. */
    public static Optional<String> detecter(byte[] b) {
        if (b == null || b.length < 5) return Optional.empty();
        if (b[0] == '%' && b[1] == 'P' && b[2] == 'D' && b[3] == 'F' && b[4] == '-')
            return Optional.of("application/pdf");
        if ((b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G')
            return Optional.of("image/png");
        if ((b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF)
            return Optional.of("image/jpeg");
        return Optional.empty();
    }

    /** Garde uniquement le nom du fichier, sans chemin ni caractère dangereux. */
    public static String nettoyerNom(String original) {
        String nom = original == null ? "" : original.replace('\\', '/');
        nom = nom.substring(nom.lastIndexOf('/') + 1).replaceAll("[^A-Za-z0-9._ -]", "_").trim();
        if (nom.isEmpty()) nom = "piece";
        return nom.length() > 200 ? nom.substring(nom.length() - 200) : nom;
    }

    public static String empreinteSha256(byte[] contenu) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenu));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }
}