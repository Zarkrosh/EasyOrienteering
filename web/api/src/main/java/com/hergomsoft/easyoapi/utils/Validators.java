package com.hergomsoft.easyoapi.utils;

public class Validators {
    
    /*************** EXPRESIONES REGULARES ***************/
    // TODO Revisar caracteres de acentuación
    private static final String REGEX_USERNAME = "^[a-zA-Z0-9_ ]+$";
    private static final String REGEX_CLUB = "^[a-zA-Z0-9_ ]*$";
    
    
    public static boolean checkCaracteresNombreUsuario(String username) {
        return username != null && username.matches(REGEX_USERNAME);
    }
    
    public static boolean checkCaracteresClub(String club ){
        return club != null && club.matches(REGEX_CLUB);
    }

}
