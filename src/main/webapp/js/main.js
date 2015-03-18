// Méthodes utiles
// JSON.stringify(json)
// JSON.parse(string)

// Données à envoyer au serveur
// 1) référencé le nom du joueur au serveur : ws.send(JSON.stringify(player)";
// 2) envoyer le déplacement du joueur : ws.send(JSON.stringify(direction));
// 3) envoyer les placements du maître : {coming soon}


/***********************************************
 * VARIABLES : utilisées dans tout le client JS
 ***********************************************/

// Adresse du serveur
var address = "localhost";
// Le joueur client
var player;
// Direction du joueur dans le labyrinthe
var direction;
// Données envoyées par le serveur via une websocket
var data;
// La websocket (instanciée uniquement au succès de la fonction de connexion)
var ws = new WebSocket("ws://" + address + ":8080/maze/websocket");
// Les items (pièges et bonus) que peut placer le maître du labyrinthe
var items = [];

/********************************
 * PLAYER : représenté par un nom
 ********************************/
 
function Player(playername) {
	this.playername = playername;
}


/***********************************************************************
 * DIRECTION : représenté par une cardinalité (NORTH, SOUTH, EAST, WEST)
 ***********************************************************************/
 
function Direction(direction) {
	this.direction = direction;
}

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
	var form = document.forms['#signin'].elements;
	$.ajax({ 
		url: "/maze/usersdb",
		type: "POST",
		dataType: "json",
		data : {
			'login' : form[0].value,
			'password' : form[1].value,
			'firstname' : form[2].value,
			'lastname' : form[3].value,
			'birthday' : form[4].value,
			'email' : form[5].value
		},
	
		success: function(data, textStatus, jqXHR) {
			alert("Création du compte : " + textStatus);
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("Erreur lors de la création du compte: " + textStatus);
		}
	});
};

/* Identifie un utilisateur dans le serveur et en ressort un joueur */
/* De la même manière, on récupèrera les données du formulaire "signup" */
/* Ce formulaire contient le login et le password de l'utilisateur */
function signup() {

	// PAS TERMINE
	
	var form = document.forms['#signup'].elements;
	$.ajax({ 
		url: "/maze/usersdb/" + form[0].value,
		type: "GET",
		dataType: "json",
		data : {
			'login' : form[0].value
		},
	
		success: function(data, textStatus, jqXHR) {
			alert("Chargement du compte : " + textStatus);
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("Erreur lors du chargement du compte: " + textStatus);
		}
	});
};

/*
 * IMPORTANT:
 *
 * Toutes les fonctions qui vont suivre ne devraient être utilisables
 * et demandées que si player !== undefined
 * donc uniquement en cas de succès de la fonction signup
 *
 */

/*************************
 * PARTIE WEBSOCKET
 *************************/

/* Fonction appelée à l'ouverture d'une websocket */
ws.onopen = function() {
	console.log("Ouverture de la websocket reussie");
};

/* Fonction appelée lorsque des données sont envoyées du serveur au client */
ws.onmessage = function (evt) {
	// Toutes les données du jeu sont envoyées par le serveur au client
	// Et sont mis au format JSON pour une meilleure manipulation
	// Important:
	// A la connexion du joueur, les données envoyées par le serveur seront le nombre de slots occupés sur le maximum
	// Lorsque l'utilisateur aura envoyé un message via ws.send(JSON.stringify(String))
	// les données envoyées par le serveur (donc reçu par tous les clients) seront les données du jeu
	// à savoir le maze, le master et les players
	data = JSON.parse(evt.data);
	// Traiter l'information selon les deux cas
	// 1) Tous les joueurs ne sont pas encore arrivés
	// 2) Tous les joueurs sont là
	alert(evt.data);
};

/* Fonction appelée lorsque la websocket est fermée */
ws.onclose = function() {
	console.log("Fermeture de la websocket reussie");
};

/* Fonction appelée en cas d'erreur dans la websocket */
ws.onerror = function(err) {
	console.log("Une erreur s'est produite dans la websocket")
	console.log(err.data);
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
