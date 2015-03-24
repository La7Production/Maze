// Méthodes utiles
// JSON.stringify(json)
// JSON.parse(string)

// Données à envoyer au serveur
// 1) référencé le nom du joueur au serveur : ws.send(JSON.stringify(player)";
// 2) envoyer le déplacement du joueur : ws.send(JSON.stringify([direction, {"pixel" : PIXEL_SIZE}]));
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
// Taille en pixel d'une case qui sera a envoyer avec la direction
var PIXEL_SIZE = 32;
// Taille en pixel de la hitbox du joueur
var HITBOX = 6;
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
// clics de l'utilisateur
var caseX = 0;
var caseY = 0;

var sources = {
        piege1: 'http://localhost:8080/img/piege1.png',
        piege2: 'http://localhost:8080/img/piege2.png'
};

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
	this.pixel = PIXEL_SIZE;
	this.hitbox = HITBOX;
}

/***********************************************
 * ITEM : représenté par un nom et une url image
 ***********************************************/

function Item(item, url, x, y) {
	this.item = item;
	this.url = url;
	this.x = x;
	this.y = y;
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
		type: "POST",
		dataType: "json",
		data : {
			// Note: le login est déjà utilisé dans l'url, on a seulement le mot de passe à envoyer en POST
			//'login' : form(0),
			'password' : form(1)
		},
		success: function(data, textStatus, jqXHR) {
			player = new Player(data.login);
			ws.send(JSON.stringify(player));
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
	
	// Traiter l'information selon les deux cas
	// 1) Tous les joueurs ne sont pas encore arrivés
	// 2) Tous les joueurs sont là
	if (data.maze !== undefined) {
		maze = data.maze;
		drawMaze();
		drawPlayers();
		drawItems();
	}
	else if (data.slots !== undefined) {
		waitPlayers(data.slots);
	}
	else if (data.winner !== undefined) {
		console.log("Vainqueur: " + data.winner);
	}
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
	var s;
	direction = null;
	switch(key) {
		case 38:s = "NORTH"; break;
		case 39:s = "EAST"; break;
		case 40:s = "SOUTH"; break;
		case 37:s = "WEST"; break;
	}
	
	if (s !== undefined) {
		direction = new Direction(s);
		ws.send(JSON.stringify(direction));
		drawPlayers();
	}
};

/* Fonction appelée lorsque l'utilisateur clique avec sa souris */
/* Cette fonction devrait être appelée uniquement si le client demandeur est le maître du labyrinthe (playername === data.master) */
function mouseClicked() {
	//TODO
	img_memory(event);
};

/* Affiche un message d'attente lorsque le nombre minimal de joueurs requis dans une partie n'est pas atteint */
function waitPlayers(slots) {
	document.getElementById("slots").innerHTML = slots;
};

/* Dessine le labyrinthe vide en considérant la variable data comme définie (data.maze.cells)*/
/* On préviligiera un parcours à 1 seul indice cells[i] et on récupèrera x et y (cells[i].x et cells[i].y) pour dessiner */
function drawMaze() {
	var canvas = document.getElementById("maze");
	var ctx = canvas.getContext("2d");
	if (imaze !== undefined) {
		ctx.fillStyle = "white";
		ctx.fillRect(0,0,ctx.canvas.width,ctx.canvas.height);
		ctx.drawImage(imaze, 0, 0);
	}
	else {
		var c;
		var cells = maze.cells;
		canvas.width = PIXEL_SIZE * maze.width;
		canvas.height = PIXEL_SIZE * maze.height;
		var draw = function(cell,mx,my,px,py) {
					ctx.moveTo(PIXEL_SIZE * cell.x + mx, PIXEL_SIZE * cell.y + my);
					ctx.lineTo(PIXEL_SIZE * cell.x + px, PIXEL_SIZE * cell.y + py);
					ctx.stroke();
				};
	
		// Entrée
		c = cells[0];
		ctx.fillStyle = "rgba(255, 0, 0, .5)"
		ctx.fillRect(PIXEL_SIZE*c.x,PIXEL_SIZE*c.y,PIXEL_SIZE,PIXEL_SIZE);
		ctx.stroke();
	
		// Sortie
		c = cells[cells.length-1];
		ctx.fillStyle = "rgba(0, 255, 0, .5)";
		ctx.fillRect(PIXEL_SIZE*c.x,PIXEL_SIZE*c.y,PIXEL_SIZE,PIXEL_SIZE);	
		ctx.stroke();
	
		for (var i=0; i < cells.length; i++) {
			c = cells[i];
			if (((c.value) & 1) === 0) {
				draw(c,0,0,PIXEL_SIZE,0);
			} 
			if (((c.value) & 2) === 0) {
				draw(c,PIXEL_SIZE,0,PIXEL_SIZE,PIXEL_SIZE);
			}
			if (((c.value) & 4) === 0) {
				draw(c,0,PIXEL_SIZE,PIXEL_SIZE,PIXEL_SIZE);
			}
			if (((c.value) & 8) === 0) {
				draw(c,0,0,0,PIXEL_SIZE);
			}
		}
	
		imaze = new Image();
		imaze.src = canvas.toDataURL("image/png");
	}
	return imaze;
};

/* Dessine le joueur avec la couleur voulue */
/* Cette méthode est aussi utilisée pour effacer les traces des déplacements précédents des joueurs */
function drawPlayer(ctx, player, color) {
	ctx.beginPath();
	ctx.arc(player.coordinates.x + HITBOX, player.coordinates.y + HITBOX, 1, 0, 2 * Math.PI);
	ctx.fillStyle = color;
	ctx.fill();
	ctx.lineWidth = 5;
	ctx.strokeStyle = color;
	ctx.stroke();
}

/* Dessine les joueurs dans le labyrinthe */
function drawPlayers() {
	var players = data.players;
	var canvas = document.getElementById("maze");
	var ctx = canvas.getContext("2d");
	var colors = ['red', 'blue', 'green', 'orange', 'grey'];
	for (var i=0; i < players.length; i++) {
		drawPlayer(ctx, players[i], colors[i%players.length]);
	}
};

/* Affiche les pièges et bonus que le maître du labyrinthe peut placer (par drag'n'drop de préférence) dans le labyrinthe */
/* Cette fonction devrait être appelée uniquement si le client demandeur est le maître du labyrinthe (playername === data.master) */
function drawItems() {
	//TODO
	var players = data.items;	
	for(var i = 0; i< 5; i++) {
		ctx.strokeRect(0, i*50,50,50);
	}
	
};


function img_memory(event){
      var x =  evenement.pageX;
      var y =  evenement.pageY;
      caseX = Math.floor(x/50);
      caseY = Math.floor(y/50)
      document.getElementById("piege1").style.left = (x+1)+'px';
      document.getElementById("piege1").style.top  = (y+1)+'px';
};

function loadImages(sources, callback) {
        var images = {};
        var loadedImages = 0;
        var numImages = 0;
        for(var src in sources) {
          numImages++;
        }
        for(var src in sources) {
          images[src] = new Image();
          images[src].onload = function() {
            if(++loadedImages >= numImages) {
              callback(images);
            }
          };
          images[src].src = sources[src];
        }
      }


