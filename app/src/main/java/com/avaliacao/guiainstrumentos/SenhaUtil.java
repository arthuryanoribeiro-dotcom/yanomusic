package com.avaliacao.guiainstrumentos;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


// Criptografia da senha: hash SHA-256 com salt aleatorio por usuario.
public final class SenhaUtil {

    private static final String ALGORITMO = "SHA-256";
    private static final int TAMANHO_SALT = 16;

    private SenhaUtil() {
    }

    public static String gerarSalt() {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hash(String senha, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO);
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] resultado = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(resultado, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 faz parte de toda JVM Android; nao deve acontecer.
            throw new IllegalStateException(e);
        }
    }

    // Login: gera o hash da senha digitada e compara com o salvo no Room.
    public static boolean verificar(String senhaDigitada, String salt, String hashSalvo) {
        byte[] calculado = hash(senhaDigitada, salt).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(calculado, hashSalvo.getBytes(StandardCharsets.UTF_8));
    }
}
