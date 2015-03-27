package fr.univ_lille1.iut_info.desquiec.mazefinal.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import fr.univ_lille1.iut_info.desquiec.mazefinal.HttpRequest.AjoutComptePost;
import fr.univ_lille1.iut_info.desquiec.mazefinal.R;
import fr.univ_lille1.iut_info.desquiec.mazefinal.Utils.Utils;


//==================================================================================================
/*  CreateUserActivity

    Représente le menu de création d'utilisateur

    Il faut remplir les champs de texte pour créer un utilisateur et valider les saisies

    1.  Activity Cycle
    2.  Buttons Actions
    3.  DialogInterface.OnClickListener

                                                                            mellardq & desquiec   */
//==================================================================================================
public class CreateUserActivity extends Activity implements DialogInterface.OnClickListener{
    private TextView login;
    private TextView password;
    private TextView prenom;
    private TextView nom;
    private TextView naissance;
    private TextView email;


//==================================================================================================
//  Activity Cycle
//==================================================================================================

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        //Instanciation des textView
        login = (TextView)findViewById(R.id.create_user_login);
        password = (TextView)findViewById(R.id.create_user_password);
        prenom = (TextView)findViewById(R.id.create_user_prenom);
        nom = (TextView)findViewById(R.id.create_user_nom);
        naissance = (TextView)findViewById(R.id.create_user_naissance);
        email = (TextView)findViewById(R.id.create_user_email);
    }

//==================================================================================================
//  Buttons Actions
//==================================================================================================

    //Crée un compte dans la base de donnée si tous les champs sont remplis
    public void valider(View view){
        //Verification si les champs sont vides
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(TextUtils.isEmpty(login.getText()) || TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(prenom.getText())
                || TextUtils.isEmpty(nom.getText()) || TextUtils.isEmpty(naissance.getText()) || TextUtils.isEmpty(email.getText())){
            Toast toast = Toast.makeText(this, R.string.emptyUser, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 150, 150);
            toast.show();

        }else{
            try {
                AjoutComptePost valide = new AjoutComptePost();
                valide.execute(login.getText().toString(), password.getText().toString(), prenom.getText().toString(), nom.getText().toString(), naissance.getText().toString(), email.getText().toString());
                if (valide.get()){
                    builder.setMessage(R.string.createUser);
                    builder.setPositiveButton(R.string.continuer, this);
                }else {
                    builder.setMessage(R.string.noServer);
                    builder.setNegativeButton(R.string.retour, this);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            //affiche une fenêtre pour confirmer que le compte est créé
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    //Réinitialise tous les champs de texte
    public void reset(View view){
        login.setText(null);
        password.setText(null);
        prenom.setText(null);
        nom.setText(null);
        naissance.setText(null);
        email.setText(null);
    }

    //Retourne au menu de connexion (LoginActivity)
    public void annuler(View view){
        this.finish();
    }


//==================================================================================================
//  DialogInterface.OnClickListener
//==================================================================================================
    //Termine l'activité pour revenir à l'écran de login.
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which==DialogInterface.BUTTON_POSITIVE){
            this.finish();
        }
    }
}