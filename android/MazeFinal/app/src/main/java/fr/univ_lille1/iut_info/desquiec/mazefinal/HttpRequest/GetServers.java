package fr.univ_lille1.iut_info.desquiec.mazefinal.HttpRequest;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import fr.univ_lille1.iut_info.desquiec.mazefinal.Utils.Utils;


//==================================================================================================
/*  GetServers

    Requête GET pour récupérer la liste des parties disponibles

                                                                            mellardq & desquiec   */
//==================================================================================================
public class GetServers extends AsyncTask<Void, Void, JSONObject>{

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            //1.   Création du client & configuration URL
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Utils.resourceURI+"servers");

            //2.   Configuration de la requête
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");

            //3.   Execution de la requête
            HttpResponse httpResponse = httpclient.execute(httpGet);

            if (httpResponse.getStatusLine().toString().equals("HTTP/1.1 404 Not Found")) {
                return null;
            } else {
                JSONObject obj = new JSONObject(Utils.convertInputStreamToString(httpResponse.getEntity().getContent()));
                return obj;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}