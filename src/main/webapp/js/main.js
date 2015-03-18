// Méthodes utiles
// JSON.stringify(json)
// JSON.parse(string)


/***********************************************
 * VARIABLES : utilisées dans tout le client JS
 ***********************************************/

// Nom du joueur client
var playername;
// Données envoyées par le serveur via une websocket
var data;
// La websocket (instanciée uniquement au succès de la fonction de connexion)
var ws;
// Les items (pièges et bonus) que peut placer le maître du labyrinthe
var items = [];

/***********************************************
 * ITEM : représenté par un nom et une url image
 ***********************************************/

function Item(name, url) {
	this.name = name;
	this.url = url;
}

/*************************
 * PARTIE REST VIA AJAX
 *************************/

/* Crée un nouvel utilisateur dans le serveur */
/* On récupèrera les données suivantes : login, password, firstname, lastname, birthday, email */
/* Ces données sont contenues dans le formulaire d'id = "signin" */
/* On peut donc parcourir le formulaire avec form[i].value (cf. fichier 'exemple_form') */
function signin() {
	var form = $("#signin");
	// TODO
};

/* Identifie un utilisateur dans le serveur et en ressort un joueur */
/* De la même manière, on récupèrera les données du formulaire "signup" */
/* Ce formulaire contient le login et le password de l'utilisateur */
playername = function signup() {
	var form = $("#signup");
	// TODO
};

/*
 * IMPORTANT:
 *
 * Toutes les fonctions qui vont suivre ne devraient être utilisables
 * et demandées que si playername !== undefined
 * donc uniquement en cas de succès de la fonction signup
 *
 */

/*************************
 * PARTIE WEBSOCKET
 *************************/

/* Fonction appelée à l'ouverture d'une websocket */
ws.onopen = function() {
	//TODO	
};

/* Fonction appelée lorsque des données sont envoyées du serveur au client */
ws.onmessage = function (evt) {
	//TODO	
};

/* Fonction appelée lorsque la websocket est fermée */
ws.onclose = function() {
	//TODO	
};

/* Fonction appelée en cas d'erreur dans la websocket */
ws.onerror = function(err) {
	//TODO	
};

/*************************
 * PARTIE GRAPHIQUE
 *************************/

/* Fonction appelée lorsque l'utilisateur appuie sur des touches précises de son clavier */
function actionPerformed() {
	//TODO
};

/* Fonction appelée lorsque l'utilisateur clique avec sa souris */
/* Cette fonction devrait être appelée uniquement si le client demandeur est le maître du labyrinthe (playername === data.master) */
function mouseClicked() {
	//TODO
};

/* Affiche un message d'attente lorsque le nombre minimal de joueurs requis dans une partie n'est pas atteint */
function waitPlayers() {
	//TODO
};

/* Dessine le labyrinthe vide en considérant la variable data comme définie (data.maze.cells)*/
/* On préviligiera un parcours à 1 seul indice cells[i] et on récupèrera x et y (cells[i].x et cells[i].y) pour dessiner */
function drawMaze() {
	var cells = data.maze.cells;
	for (var i=0; i < cells.length; i++) {
		// cells[i].x
		// cells[i].y
		// TODO
	}
};

/* Dessine les joueurs dans le labyrinthe */
function drawPlayers() {
	var players = data.players;
	var master = data.master;
	// TODO
};

/* Affiche les pièges et bonus que le maître du labyrinthe peut placer (par drag'n'drop de préférence) dans le labyrinthe */
/* Cette fonction devrait être appelée uniquement si le client demandeur est le maître du labyrinthe (playername === data.master) */
function drawItems() {
	//TODO
};
