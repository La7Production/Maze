package fr.univ_lille1.iut_info.desquiec.mazefinal.HttpRequest;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

import fr.univ_lille1.iut_info.desquiec.mazefinal.Utils.Utils;


//==================================================================================================
/*  PutPassword

    Change le mot de passe de l'utilisateur dans la base de donnée

                                                                            mellardq & desquiec   */
//==================================================================================================
public class PutPassword extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        try {
            //1.   Création du client & configuration URL
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut(Utils.resourceURI+"usersdb/"+params[0]);

            //2.    Création du JSON à envoyer
            String json = "";
            JSONObject obj = new JSONObject();
            obj.put("login", params[0]);
            obj.put("password", params[1]);
            obj.put("firstname", params[2]);
            obj.put("lastname", params[3]);
            obj.put("birthday", params[4]);
            obj.put("email", params[5]);

            //3.    JSONObject -> String
            json = obj.toString();

            //4.    Configuration de la requête
            StringEntity se = new StringEntity(json);
            httpPut.setEntity(se);
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");

            //5.    Execution de la requête
            HttpResponse httpResponse = httpclient.execute(httpPut);

            if (!httpResponse.getStatusLine().toString().equals("HTTP/1.1 404 Not Found"))
                return true;
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}