package com.hergomsoft.easyoapi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;

public class Utils {
    /**
     * Genera una cadena aleatoria del tamaño especificado.
     * Creds: https://www.baeldung.com/java-random-string
     * @param n Tamaño de la cadena
     * @return Cadena aleatoria de  tamaño n
     */
    public static String cadenaAleatoria(int n) {
        int leftLimit = 48; // '0'
        int rightLimit = 122; // 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .limit(n)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

        return generatedString;
    }
    
    /**
     * Devuelve el hash MD5 de la cadena especificada.
     * Creds: https://www.baeldung.com/java-md5
     * @param cadena Cadena a computar
     * @return MD5 de la cadena
     */
    public static String md5(String cadena) {
        if(cadena != null) {
            String res = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(cadena.getBytes());
                byte[] digest = md.digest();
                res = DatatypeConverter.printHexBinary(digest).toUpperCase();
            } catch (NoSuchAlgorithmException e) {}

            return res;
        } else {
            throw new IllegalArgumentException("Error al obtener el MD5. La cadena no puede ser null.");
        }
    }
    
    /**
     * Devuelve el hash SHA-256 de la cadena especificada.
     * Creds: https://www.baeldung.com/sha-256-hashing-java
     * @param cadena Cadena a computar
     * @return SHA-256 de la cadena
     */
    public static String sha256(String cadena) {
        if(cadena != null) {
            String res = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(cadena.getBytes());
                byte[] digest = md.digest();
                res = DatatypeConverter.printHexBinary(digest).toUpperCase();
            } catch (NoSuchAlgorithmException e) {}

            return res;
        } else {
            throw new IllegalArgumentException("Error al obtener el SHA-256. La cadena no puede ser null.");
        }
    }
}
