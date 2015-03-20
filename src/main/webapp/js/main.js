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
var address = location.hostname;
// Le joueur client
var player;
// Direction du joueur dans le labyrinthe
var direction;
// Données envoyées par le serveur via une websocket
var data;
// Labyrinthe récupéré dans les données
var maze;
// Image du labyrinthe
var imaze;
// La websocket (instanciée uniquement au succès de la fonction de connexion)
var ws = new WebSocket("ws://" + address + ":8080/maze/websocket");
// Les items (pièges et bonus) que peut placer le maître du labyrinthe
var items = [];

window.addEventListener('keydown', actionPerformed, true);

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
/* Ces données sont contenues dans le div d'id = "signin" */
/* On peut donc parcourir le div comme un formulaire avec form[i].value (cf. fichier 'exemple_form') */
function signup() {	
	var form = function(i) { return document.getElementById("signup").children.item(i).children[0].value; }
	$.ajax({
		url: "/maze/usersdb",
		type: "POST",
		// Attention: coté serveur on retourne la réponse HTTP, pas un objet parseable en JSON !
		//dataType: "json",
		data : {
			'login' : form(0),
			'password' : form(1),
			'firstname' : form(2),
			'lastname' : form(3),
			'birthday' : form(4),
			'email' : form(5)
		},
	
		success: function(data, textStatus, jqXHR) {
			document.getElementById("notif").innerHTML = "Votre compte est désormais créé";
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("notif").innerHTML = "Erreur lors du chargement du compte: " + errorThrown;
		}
	});
};

/* Identifie un utilisateur dans le serveur et en ressort un joueur */
/* De la même manière, on récupèrera les données du div "signup" */
/* Ce div servant de formulaire contient le login et le password de l'utilisateur */
function signin() {
	var form = function(i) { return document.getElementById("signin").children.item(i).children[0].value; }
	$.ajax({
		url: "/maze/usersdb/" + form(0),
		type: "GET",
		dataType: "json",
		data : {
			'login' : form(0)
		},
		success: function(data, textStatus, jqXHR) {
			player = new Player(data.login);
			ws.send(JSON.stringify(player));
			ws.send("{direction: NORTH}");
			document.getElementById("notif").innerHTML = "Bonjour " + data.login;
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("notif").innerHTML = "Nom d'utilisateur ou mot de passe incorrect";
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
	
	if (data.maze !== undefined) {
		maze = data.maze;
		drawMaze();
		drawPlayers();
	}
	
	// Traiter l'information selon les deux cas
	// 1) Tous les joueurs ne sont pas encore arrivés
	// 2) Tous les joueurs sont là
	document.getElementById("slots").innerHTML = evt.data;
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
function actionPerformed(evt) {
	//90 68 83 81
	var key = evt.keyCode;
	direction = null;
	if (key === 90) { direction = new Direction("NORTH"); }
	else if (key === 68) { direction = new Direction("EAST"); }
	else if (key === 83) { direction = new Direction("SOUTH"); }
	else if (key === 81) { direction = new Direction("WEST"); }
	
	console.log(direction);
	ws.send(JSON.parse(direction));
	drawPlayers();
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
	var c;
	var cells = maze.cells;
	var canvas = document.getElementById("maze");
	var ctx = canvas.getContext("2d");
	ctx.canvas.width = maze.pixel * maze.width;
	ctx.canvas.height = maze.pixel * maze.height;
	var draw = function(cell,mx,my,px,py) {
				ctx.moveTo(maze.pixel * cell.x + mx, maze.pixel * cell.y + my);
				ctx.lineTo(maze.pixel * cell.x + px, maze.pixel * cell.y + py);
				ctx.stroke();
			};
	
	// Entrée
	c = cells[0];
	ctx.fillStyle = "rgba(255, 0, 0, .5)"
	ctx.fillRect(maze.pixel*c.x,maze.pixel*c.y,maze.pixel,maze.pixel);	
	ctx.stroke();
	
	// Sortie
	c = cells[cells.length-1];
	ctx.fillStyle = "rgba(0, 255, 0, .5)";
	ctx.fillRect(maze.pixel*c.x,maze.pixel*c.y,maze.pixel,maze.pixel);	
	ctx.stroke();
	
	for (var i=0; i < cells.length; i++) {
		c = cells[i];
		if (((c.value) & 1) === 0) {
			draw(c,0,0,maze.pixel,0);
		} 
		if (((c.value) & 2) === 0) {
			draw(c,maze.pixel,0,maze.pixel,maze.pixel);
		}
		if (((c.value) & 4) === 0) {
			draw(c,0,maze.pixel,maze.pixel,maze.pixel);
		}
		if (((c.value) & 8) === 0) {
			draw(c,0,0,0,maze.pixel);
		}
	}
	
	imaze = new Image();
	imaze.src = canvas.toDataURL("image/png");
	return imaze;
};

/* Dessine les joueurs dans le labyrinthe */
function drawPlayers() {
	var players = data.players;
	var canvas = document.getElementById("maze");
	var ctx = canvas.getContext("2d");
	var xp;
	var yp;
	var p = 10;
	for (var i=0; i < players.length; i++) {
		xp = players[i].coordinates.x;
		yp = players[i].coordinates.y;
		ctx.beginPath();
		ctx.fillStyle = "blue";
		ctx.fill();
		ctx.arc(xp, yp, 5, 0, 2 * Math.PI);
		ctx.stroke();
	}
};

/* Affiche les pièges et bonus que le maître du labyrinthe peut placer (par drag'n'drop de préférence) dans le labyrinthe */
/* Cette fonction devrait être appelée uniquement si le client demandeur est le maître du labyrinthe (playername === data.master) */
function drawItems() {
	//TODO
};
