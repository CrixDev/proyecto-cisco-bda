package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad de seguridad: encripta contraseñas con SHA-256.
 *
 * El hash es de una sola vía (no se puede descifrar); para verificar una
 * contraseña se vuelve a hashear el texto ingresado y se compara contra el
 * hash almacenado en la base de datos.
 */
public final class Encriptador {

    private Encriptador() {
    }

    /** Devuelve el hash SHA-256 (hexadecimal, 64 caracteres) del texto dado. */
    public static String encriptar(String texto) {
        if (texto == null) {
            texto = "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No se encontró el algoritmo SHA-256", e);
        }
    }

    /** Compara un texto plano contra un hash almacenado (ambos se normalizan con trim). */
    public static boolean coincide(String plano, String hashAlmacenado) {
        if (plano == null || hashAlmacenado == null) {
            return false;
        }
        return encriptar(plano.trim()).equalsIgnoreCase(hashAlmacenado.trim());
    }
}
