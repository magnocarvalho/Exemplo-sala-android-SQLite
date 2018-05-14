package br.edu.utfpr.alexandrefeitosa.sqlite.utils;

public class UtilsString {

    public static boolean stringVazia(String texto){

        if (texto == null || texto.trim().length() == 0){
            return true;
        }else{
            return false;
        }
    }
}
