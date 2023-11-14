package br.com.sankhya.bhz.utils;

public class ErroUtils {

    public static void disparaErro(String msg) throws Exception {
        String msgTratada = "<hr>" +
                "<b><span style=\"font-size: 1.2em\">" + msg + "</span></b><hr>";
        throw new Exception(msgTratada);
    }
    public static void ErroFormatado(String msg, String motivo, String solucao) throws Exception {
        String m = null;
        String s= null;
        if(motivo != null){
            m = "<b>Motivo: </b> "+motivo+ "<br><br>";
        }else {
            m = "<br><br>";
        }
        if(solucao != null){
            s = "<b>Solução: </b> "+solucao+ "</span></b><hr>";
        }else {
            s = "</span></b><hr>";
        }
        String msgTratada = "<hr>" +
                "<p align=\"left\"><font size=\"3\" face=\"arial\" color=\"#8B1A1A\"><b>Atenção:  </b>" + msg +"<b>  --Ação Cancelada--</b>  <br><br>"
                +m
                +s;
        throw new Exception(msgTratada);
    }
}