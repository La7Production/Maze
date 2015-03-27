package fr.univ_lille1.iut_info.desquiec.mazefinal.HttpRequest;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.desquiec.mazefinal.Utils.Utils;


//==================================================================================================
/*  PostLogin

    Vérifie l'existence de l'utilisateur dans la base de donnée

                                                                            mellardq & desquiec   */
//==================================================================================================
public class PostLogin extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            //1.   Création du client & configuration URL
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Utils.resourceURI+"usersdb/"+params[0]);

            //2.   Création des formulaire
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("password", params[1]));

            //3.   Configuration de la requête
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

            //4.   Execution de la requête
            HttpResponse httpResponse = httpclient.execute(httpPost);

            if (!httpResponse.getStatusLine().toString().equals("HTTP/1.1 404 Not Found")) {
                JSONObject receive = new JSONObject(Utils.convertInputStreamToString(httpResponse.getEntity().getContent()));
                return receive;
            }else
                return  null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}