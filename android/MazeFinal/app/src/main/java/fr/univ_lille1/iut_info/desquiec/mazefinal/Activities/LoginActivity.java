package fr.univ_lille1.iut_info.desquiec.mazefinal.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import fr.univ_lille1.iut_info.desquiec.mazefinal.HttpRequest.PostLogin;
import fr.univ_lille1.iut_info.desquiec.mazefinal.R;

//==================================================================================================
/*  LoginActivity

    Représente l'écran de connexion qui s'affiche au lancement de l'application

    Cette activité amène soit à la création de compte (CreateUserActivity) soit au profil de
    l'utilisateur (PlayerMenuActivity).

    1.  Activity Cycle
    2.  Buttons Actions

                                                                            mellardq & desquiec   */
//==================================================================================================

public class LoginActivity extends Activity {

//==================================================================================================
//  Activity Cycle
//==================================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//==================================================================================================
//  Buttons Actions
//==================================================================================================

    //Amène à l'activité CreateUserActivity
    public void createUser(View view){
        Intent i = new Intent(this, CreateUserActivity.class);
        startActivity(i);
    }

    //Vérifie les champs de connexion si ils sont valides elle connecte l'utilisateur
    //sinon affiche un Toast contenant l'erreur
    public void login(View view){
        TextView login = (TextView)findViewById(R.id.identifiant);
        TextView password = (TextView)findViewById(R.id.password);

        Intent i = new Intent(this, PlayerMenuActivity.class);
        i.putExtra("login", login.getText().toString());
        i.putExtra("password", password.getText().toString());
        if(!i.getStringExtra("login").equals("")) {
            PostLogin valide = new PostLogin();
            //Execute la requete POST pour verifier si le joueur existe et reçoit l'ensemble des
            //infos du joueur
            valide.execute(login.getText().toString(), password.getText().toString());
            try {
                if(valide.get()!=null){
                    i.putExtra("lastname", valide.get().getString("lastname"));
                    i.putExtra("firstname", valide.get().getString("firstname"));
                    i.putExtra("email", valide.get().getString("email"));
                    i.putExtra("birthday", valide.get().getString("birthday"));
                    startActivity(i);
                }else{
                    //Toast affiche une bulle de texte qui disparait après quelques secondes
                    Toast toast = Toast.makeText(this, R.string.id_mdp_incorrects, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP| Gravity.LEFT, 200, 500);
                    toast.show();
                }
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
