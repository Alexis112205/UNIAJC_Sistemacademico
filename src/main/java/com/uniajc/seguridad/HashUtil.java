package com.uniajc.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad de seguridad para generación y verificación de contraseñas
 * mediante funciones hash criptográficas (SHA-512 y MD5) solicitadas en la rúbrica.
 */
public final class HashUtil {

    private HashUtil() {
    }

    /**
     * Genera el hash de una contraseña o texto usando SHA-512 (128 caracteres hexadecimales).
     *
     * @param textoPlano Contraseña o dato en texto plano.
     * @return Hash en formato hexadecimal en minúsculas.
     */
    public static String sha512(String textoPlano) {
        return hashear(textoPlano, "SHA-512");
    }

    /**
     * Genera el hash de una contraseña o texto usando MD5 (32 caracteres hexadecimales).
     *
     * @param textoPlano Contraseña o dato en texto plano.
     * @return Hash en formato hexadecimal en minúsculas.
     */
    public static String md5(String textoPlano) {
        return hashear(textoPlano, "MD5");
    }

    /**
     * Verifica si una contraseña ingresada coincide con un hash SHA-512 almacenado.
     *
     * @param textoPlano  Contraseña a verificar.
     * @param hashEsperado Hash SHA-512 contra el cual comparar.
     * @return true si coinciden, false en caso contrario.
     */
    public static boolean verificarSha512(String textoPlano, String hashEsperado) {
        if (textoPlano == null || hashEsperado == null) {
            return false;
        }
        return sha512(textoPlano).equalsIgnoreCase(hashEsperado.trim());
    }

    /**
     * Verifica si una contraseña ingresada coincide con un hash MD5 almacenado.
     *
     * @param textoPlano  Contraseña a verificar.
     * @param hashEsperado Hash MD5 contra el cual comparar.
     * @return true si coinciden, false en caso contrario.
     */
    public static boolean verificarMd5(String textoPlano, String hashEsperado) {
        if (textoPlano == null || hashEsperado == null) {
            return false;
        }
        return md5(textoPlano).equalsIgnoreCase(hashEsperado.trim());
    }

    private static String hashear(String entrada, String algoritmo) {
        if (entrada == null) {
            throw new IllegalArgumentException("La entrada no puede ser nula.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algoritmo);
            byte[] hashBytes = digest.digest(entrada.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de encriptación no disponible: " + algoritmo, e);
        }
    }
}
