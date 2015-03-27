package fr.univ_lille1.iut_info.desquiec.mazefinal.Websocket;

import android.os.AsyncTask;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import fr.univ_lille1.iut_info.desquiec.mazefinal.Activities.DrawActivity;
import fr.univ_lille1.iut_info.desquiec.mazefinal.Utils.Utils;


//==================================================================================================
/*  AsyncWebSocket

    Class asynchrone utilisant une websocket pour se connecter à une partie. A chaque fois qu'elle
    reçoit un message du serveur, elle publie l'objet JSON reçue à la DrawActivity qui interpète le
    résultat.

                                                                            mellardq & desquiec   */
//==================================================================================================

public class AsyncWebSocket extends AsyncTask<String, String, Void> {
    private DrawActivity activity;
    private WebSocket.Connection connection = null;
    private String slots;

    public AsyncWebSocket(DrawActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(final String... params) {
        try {
            //Connection à la websocket
            WebSocketClientFactory factory = new WebSocketClientFactory();
            factory.start();

            final WebSocketClient ws = factory.newWebSocketClient();

            URI uri = new URI(Utils.webURI + "websocket/" + params[0]); //Params[0] = nom de la partie

            try {
                this.connection = ws.open(uri, new WebSocket.OnTextMessage() {

                    //Lorsque la connexion est ouverte, on envoie que l'on est un observer
                    public void onOpen(Connection connection) {
                        try {
                            connection.sendMessage("{observer: "+params[1]+"}");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onClose(int closeCode, String message) {

                    }

                    //Publication du message si un message un reçu, fermeture de la connexion si
                    //l'asynctask est cancelled.
                    public void onMessage(String data) {
                        if(isCancelled()){
                            connection.close();
                        }
                        publishProgress(data);

                    }
                }).get(5, TimeUnit.SECONDS); //Si au bout de 5 secondes, aucun message n'est reçu, deconnexion automatique

            } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        JSONObject receive;     //Ensemble de l'objet JSON reçu (Slots, CellSize, Players, Maze)
        JSONObject maze;        //Ensemble de l'objet Maze reçu en JSON

        try {
            receive = new JSONObject(values[0]);

            //Tant que la partie n'est pas lancée (tant que tous les joueurs ne sont pas dans la partie)
            //On reçoit le nombre de slots occupés, ainsi que la taille des case du serveur
            if (receive.has("cellsize")) {
                activity.setServerCellSize(receive.getInt("cellsize"));
                this.slots = receive.getString("slots");
            }

            //Une fois la partie lancée, on reçoit le labyrinthe et la liste des joueurs avec leurs coordonnées
            //Toutefois, si le labyrinthe envoyé par le serveur contient trop de caractère (pusiqu'il est envoyé en JSON)
            //Il y a aura une erreur car trop de caractères sont reçus
            else if (receive.has("maze") && receive.has("players")) {
                try {
                    maze = receive.getJSONObject("maze");
                    activity.setClientCellSize(Utils.getScreenWidth() / maze.getInt("width"));
                    activity.draw(maze, receive.getJSONArray("players"));
                }catch(JSONException e){
                    activity.drawNbPlayers(this.slots);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


