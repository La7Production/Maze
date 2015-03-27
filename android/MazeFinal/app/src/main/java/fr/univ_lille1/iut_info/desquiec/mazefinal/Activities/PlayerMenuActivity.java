package fr.univ_lille1.iut_info.desquiec.mazefinal.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import fr.univ_lille1.iut_info.desquiec.mazefinal.HttpRequest.GetServers;
import fr.univ_lille1.iut_info.desquiec.mazefinal.HttpRequest.PutPassword;
import fr.univ_lille1.iut_info.desquiec.mazefinal.R;
import fr.univ_lille1.iut_info.desquiec.mazefinal.Utils.Utils;


//==================================================================================================
/*  PlayerMenuActivity

    Représente le menu d'un utilisateur. Composée de deux onglets, profil et observer.

    Profil: regroupe les informations sur le compte de l'utilisateur comme son pseudo, mdp, nom,
            mais aussi les informations relatives aux parties jouées par l'utilisateur comme ses
            taux de victoires, son nombre de partie jouées.

    Observer:   permet à l'utilisateur de choisir la partie qu'il veut observer parmi une liste de
                toutes les parties en cours.

    1.  Activity Cycle
    2.  Private Methods
    3.  Buttons Actions
    4.  DialogInterface.OnClickListener

                                                                            mellardq & desquiec   */
//==================================================================================================

public class PlayerMenuActivity extends Activity implements DialogInterface.OnClickListener{
    private String login = null;
    private String password = null;
    private String firstname = null;
    private String lastname = null;
    private String birthday = null;
    private String emailAddr= null;
    private String oldPass;
    private String newPass;
    private String confirmPass;
    private TextView oldPassword;
    private TextView newPassword;
    private TextView confirmPassword;


//==================================================================================================
//  Activity Cycle
//==================================================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();

        //Remplit les champs login et password avec ceux entrés et validés
        this.login = i.getStringExtra("login");
        this.password = i.getStringExtra("password");
        this.firstname = i.getStringExtra("firstname");
        this.lastname = i.getStringExtra("lastname");
        this.birthday = i.getStringExtra("birthday");
        this.emailAddr = i.getStringExtra("email");
        initPlayerMenu();
        actualiser(this.getCurrentFocus());

    }



//==================================================================================================
//  Private Methods
//==================================================================================================
    //Initialise l'affichage du layout.
    private void initPlayerMenu(){
        setContentView(R.layout.player_menu);

        ViewSwitcher vs = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        Button profil_button = (Button) findViewById(R.id.profil_button);
        Button observer_button = (Button) findViewById(R.id.observer_button);
        TextView pseudoJoueur = (TextView) findViewById(R.id.pseudoJoueur);
        TextView prenom = (TextView) findViewById(R.id.prenomJoueur);
        TextView nom = (TextView) findViewById(R.id.nomJoueur);
        TextView naissance = (TextView) findViewById(R.id.dateJoueur);
        TextView email = (TextView) findViewById(R.id.emailJoueur);


        prenom.setText(firstname);
        nom.setText(lastname);
        naissance.setText(birthday);
        email.setText(emailAddr);
        pseudoJoueur.setText(login);

        profil_button.setEnabled(false);
        profil_button.setWidth(Utils.getScreenWidth()/2);
        observer_button.setWidth(Utils.getScreenWidth()/2);
    }

//==================================================================================================
//  Buttons Actions
//==================================================================================================
    //Lance une DrawActivity sur la partie sélectionnée avec le spinner
    public void startObserving(View view){
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        Intent i = new Intent(this, DrawActivity.class);
        i.putExtra("serverName", spinner.getSelectedItem().toString());
        i.putExtra("playerName", login);

        startActivity(i);
    }

    //Actualise la liste des parties disponibles dans le spinner
    public void actualiser(View view) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> serverList = new ArrayList<String>();
        GetServers gs = new GetServers();
        try {
            //Récupération de la liste des parties
            JSONArray servers = gs.execute().get().getJSONArray("servers");
            if (servers != null) {
                for(int i = 0; i<servers.length();i++) {
                    serverList.add(servers.getJSONObject(i).getString("title"));
                }
            }
            //Instanciation de l'adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, serverList);
            spinner.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    //Met la vue Observer au premier plan
    public void switch_view_observer(View view){
        ViewSwitcher vs = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        Button bp = (Button) findViewById(R.id.profil_button);
        Button bo = (Button) findViewById(R.id.observer_button);

        bp.setEnabled(true);
        bo.setEnabled(false);

        vs.showNext();
    }

    //Met la vue Profil au premier plan
    public void switch_view_profil(View view){
        ViewSwitcher vs = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        Button bp = (Button) findViewById(R.id.profil_button);
        Button bo = (Button) findViewById(R.id.observer_button);

        bp.setEnabled(false);
        bo.setEnabled(true);

        vs.showPrevious();
    }

    //Ouvre une fenêtre pour modifier le mot de passe de l'utilisateur quand on appuie
    //sur le bouton "changer mot de passe"
    public void change_password(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        oldPassword = new EditText(PlayerMenuActivity.this);
        confirmPassword = new EditText(PlayerMenuActivity.this);
        newPassword = new EditText(PlayerMenuActivity.this);

        oldPassword.setHint(R.string.oldPassword);
        newPassword.setHint(R.string.newPassword);
        confirmPassword.setHint(R.string.confirmPassword);

        oldPassword.setInputType(129);
        newPassword.setInputType(129);
        confirmPassword.setInputType(129);

        layout.addView(oldPassword);
        layout.addView(newPassword);
        layout.addView(confirmPassword);

        builder.setView(layout);

        builder.setPositiveButton(R.string.changeMdp, this);
        builder.setNegativeButton(R.string.retour, this);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


//==================================================================================================
//  DialogInterface.OnClickListener
//==================================================================================================
    //si l'ancien mot de passe es bon et que les nouveaux se correspondent, change le mot de passe
    //de l'utilisateur dans la base de donnée
    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Change le mot de passe
        if(which==DialogInterface.BUTTON_POSITIVE) {
            oldPass = oldPassword.getText().toString();
            newPass = newPassword.getText().toString();
            confirmPass = confirmPassword.getText().toString();

            if (oldPass.compareTo("") == 0 && newPass.compareTo("") == 0 && confirmPass.compareTo("") == 0) {
                Toast.makeText(getApplicationContext(), R.string.emptyUser, Toast.LENGTH_SHORT).show();
            } else if (!password.equals(oldPass)) {
                Toast.makeText(getApplicationContext(), R.string.wrongPassword, Toast.LENGTH_SHORT).show();
            } else if (!newPass.equals(confirmPass)) {
                Toast.makeText(getApplicationContext(), R.string.wrongMatch, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    //Lancement de la requête PUT
                    PutPassword valide = new PutPassword();
                    valide.execute(login, newPass, firstname, lastname, birthday, emailAddr);
                    if (valide.get()) {
                        Toast.makeText(getApplicationContext(), R.string.changedPassword, Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}