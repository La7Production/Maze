package fr.univ_lille1.iut_info.desquiec.mazefinal.Utils;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//==================================================================================================
/*  Utils

    Contient les méthodes utilitaires.
        Largeur d'écran
        Hauteur d'écran
        Transcription d'un inputStream en String
        Adresses auxquelles les requêtes doivent se connecter

        Localhost sous VM Android : 10.0.2.2

                                                                            mellardq & desquiec   */
//==================================================================================================
public class Utils {
    //Permet de changer les adresses de toutes les requêtes
    private static String address = "10.0.2.2:8080";
    public static String resourceURI = "http://"+address+"/maze/";
    public static String webURI = "ws://"+address+"/maze/";


    //Retourne la hauteur de l'écran
    public static int getScreenHeight(){
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return dm.heightPixels;
    }

    //Retourne la largeur de l'écran
    public static int getScreenWidth(){
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return dm.widthPixels;
    }

    //Convertit un InputStream en String
    public static String convertInputStreamToString(InputStream inputStream) throws IOException{

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}