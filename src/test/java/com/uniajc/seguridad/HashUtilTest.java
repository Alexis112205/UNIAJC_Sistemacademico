package com.uniajc.seguridad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Seguridad - HashUtil (SHA-512 y MD5)")
class HashUtilTest {

    @Test
    @DisplayName("Debe generar hash SHA-512 de 128 caracteres hexadecimales")
    void debeGenerarHashSha512Valido() {
        String clave = "Admin123*";
        String hash = HashUtil.sha512(clave);

        assertNotNull(hash);
        assertEquals(128, hash.length());
        assertTrue(HashUtil.verificarSha512(clave, hash));
        assertFalse(HashUtil.verificarSha512("otra_clave", hash));
    }

    @Test
    @DisplayName("Debe generar hash MD5 de 32 caracteres hexadecimales")
    void debeGenerarHashMd5Valido() {
        String clave = "Profesor2026";
        String hash = HashUtil.md5(clave);

        assertNotNull(hash);
        assertEquals(32, hash.length());
        assertTrue(HashUtil.verificarMd5(clave, hash));
        assertFalse(HashUtil.verificarMd5("clave_erronea", hash));
    }

    @Test
    @DisplayName("El cálculo de hash debe ser determinista para el mismo texto")
    void debeSerDeterminista() {
        String texto = "estudiante_uniajc";
        assertEquals(HashUtil.sha512(texto), HashUtil.sha512(texto));
        assertEquals(HashUtil.md5(texto), HashUtil.md5(texto));
    }

    @Test
    @DisplayName("Entrada nula debe lanzar IllegalArgumentException")
    void entradaNulaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> HashUtil.sha512(null));
        assertThrows(IllegalArgumentException.class, () -> HashUtil.md5(null));
    }
}
