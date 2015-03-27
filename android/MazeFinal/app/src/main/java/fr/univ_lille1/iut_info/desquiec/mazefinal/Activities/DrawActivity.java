package fr.univ_lille1.iut_info.desquiec.mazefinal.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.univ_lille1.iut_info.desquiec.mazefinal.R;
import fr.univ_lille1.iut_info.desquiec.mazefinal.Utils.Utils;
import fr.univ_lille1.iut_info.desquiec.mazefinal.Websocket.AsyncWebSocket;


//==================================================================================================
/*  DrawActivity

    Affiche le plateau de jeu ou le nombre de joueur à attendre dans un canvas. S'actualise
    lorsqu'un message est reçu du serveur.

    1.  Activity Cycle
    2.  Drawing Methods
    3.  Utils
    4.  Setters

                                                                            mellardq & desquiec   */
//==================================================================================================
public class DrawActivity extends Activity {
    private Paint walls;
    private Paint start;
    private Paint exit;
    private AsyncWebSocket aws;
    private int clientCellSize;
    private int serverCellSize;
    private String serverName;
    private String playerName;


//==================================================================================================
//  Activity Cycle
//==================================================================================================
    //A la création on se connecte à la websocket
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_activity);
        initPaint();
        Intent i = getIntent();
        this.serverName = i.getStringExtra("serverName");
        this.playerName = i.getStringExtra("playerName");
        aws = new AsyncWebSocket(this);
        aws.execute(this.serverName, this.playerName);
    }

    //Recréation d'une websocket et reprise de l'activité
    @Override
    public void onResume(){
        super.onResume();
        if(aws.isCancelled()) {
            aws = new AsyncWebSocket(this);
            aws.execute(this.serverName,this.playerName );
        }

    }

    //Destruction de la websocket et mise en pause de l'activité
    @Override
    public void onPause(){
        super.onPause();
        aws.cancel(true);


    }

    //Destruction de la websocket et de l'activité
    public void onDestroy(){
        super.onDestroy();
        aws.cancel(true);
    }
//==================================================================================================
//  Drawing methods
//==================================================================================================

    //Methode de dessin principal
    //Créer un nouveau canva dans lequel on va dessiner les cases de depart, les murs puis les joueurs
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void draw(JSONObject maze, JSONArray players) {
        Bitmap bitmap = Bitmap.createBitmap(Utils.getScreenWidth(), Utils.getScreenHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        try{
            drawStartExit(maze, canvas);
            drawWalls(canvas, maze, players);
            drawPlayers(canvas, players);
        }catch(JSONException e){
            e.printStackTrace();
        }

        LinearLayout ll = (LinearLayout)findViewById(R.id.drawZone);
        ll.setBackground(new BitmapDrawable(getResources(), bitmap));
    }


    //Dessine les cases de depart et d'arrivee
    private void drawStartExit(JSONObject maze, Canvas canvas) throws JSONException{
        int x, y;

        //Depart
        x = maze.getJSONObject("start").getInt("x");
        y = maze.getJSONObject("start").getInt("y");
        canvas.drawRect(x * clientCellSize, y * clientCellSize, (x + 1) * clientCellSize, (y + 1) * clientCellSize, this.start);

        //Arrivee
        x = maze.getJSONObject("exit").getInt("x");
        y = maze.getJSONObject("exit").getInt("y");
        canvas.drawRect(x * clientCellSize, y * clientCellSize, (x + 1) * clientCellSize, (y + 1) * clientCellSize, this.exit);
    }


    //Dessine les murs du labyrinthe
    private void drawWalls(Canvas canvas, JSONObject maze, JSONArray players) throws JSONException {
        JSONArray cells = maze.getJSONArray("cells");
        int x = 0;
        int y = 0;
        int value = 0;

        //Boucle de dessin des murs
        for (int i = 0; i < cells.length(); i++) {
            try {
                x = cells.getJSONObject(i).getInt("x");
                y = cells.getJSONObject(i).getInt("y");
                value = cells.getJSONObject(i).getInt("value");

                //North wall
                if ((value & 1) == 0) {
                    canvas.drawLine(x * clientCellSize, y * clientCellSize, (x + 1) * clientCellSize, y * clientCellSize, walls);
                }

                //East wall
                if ((value & 2) == 0) {
                    canvas.drawLine((x + 1) * clientCellSize, y * clientCellSize, (x + 1) * clientCellSize, (y + 1) * clientCellSize, walls);
                }

                //South wall
                if ((value & 4) == 0) {
                    canvas.drawLine(x * clientCellSize, (y + 1) * clientCellSize, (x + 1) * clientCellSize, (y + 1) * clientCellSize, walls);
                }

                //West wall
                if ((value & 8) == 0) {
                    canvas.drawLine(x * clientCellSize, y * clientCellSize, x * clientCellSize, (y + 1) * clientCellSize, walls);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //Dessiner les joueurs dans le canvas donnée
    private void drawPlayers(Canvas canvas, JSONArray players) {
        int x=0;
        int y=0;
        String name=null;
        Paint player = new Paint();
        for(int i = 0; i<players.length(); i++) {
            try {
                Color c = new Color();
                player.setColor(c.parseColor(players.getJSONObject(i).getString("color")));
                player.setTextSize(25);
                player.setTextAlign(Paint.Align.CENTER);
                x = players.getJSONObject(i).getJSONObject("coordinates").getInt("x") * ratio();
                y = players.getJSONObject(i).getJSONObject("coordinates").getInt("y") * ratio();
                name = players.getJSONObject(i).getString("name");
                canvas.drawRect(x, y, x + ratio(), y + ratio(), player);
                canvas.drawText(name,x, y-5,player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Affiche le nombre de joueurs restants avant que la partie ne commence
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void drawNbPlayers(String s) {
        Bitmap bitmap = Bitmap.createBitmap(Utils.getScreenWidth(), Utils.getScreenHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(50);
        text.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("En attente de joueurs. . . " + s, Utils.getScreenWidth()/2, 100, text);

        LinearLayout ll = (LinearLayout)findViewById(R.id.drawZone);
        ll.setBackground(new BitmapDrawable(getResources(), bitmap));
    }

//==================================================================================================
//  Utils
//==================================================================================================

    //Retourne le ratio entre le taille des cellules du serveur et la taille des cellules du client
    //pour afficher les personnages aux bonnes coordonnées
    private int ratio(){
        return clientCellSize/serverCellSize;
    }

    //Definition de la manière de peindre les différents composants du labyrinthe
    private void initPaint(){
        //Murs
        this.walls = new Paint();
        walls.setColor(Color.BLACK);
        walls.setStrokeWidth(2);
        walls.setAntiAlias(true);

        //Case de départ
        this.start = new Paint();
        start.setColor(Color.GREEN);

        //Case d'arrivée
        this.exit = new Paint();
        exit.setColor(Color.RED);
    }


//==================================================================================================
//  Setters
//==================================================================================================

    public void setServerCellSize (int i) {
        this.serverCellSize = i;
    }

    public void setClientCellSize (int i) {
        this.clientCellSize = i;
    }


}