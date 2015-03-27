/***********************************************
 * VARIABLES : utilisées dans tout le client JS
 ***********************************************/

// Le joueur client
var player;
// Taille en pixel d'une case coté client
var PIXEL_SIZE;
// Taille d'une case en pixel coté serveur servant de référence pour le client
var SERVER_CELL_SIZE;
// Ratio pour un affichage correct
var ratio = function() { return PIXEL_SIZE/SERVER_CELL_SIZE; };
// Données envoyées par le serveur via une websocket
var data;
// Image du labyrinthe
var imaze;
// La websocket (instanciée lorsque le joueur rejoins une partie)
var ws;
// Les items (pièges et bonus) que peut placer le maître du labyrinthe
var items = [];
// Touches du clavier disponibles pour l'application
var map = {37:false, 38:false, 39:false, 40:false};
// Timer pour les touches
var timer;

//window.addEventListener('keydown', actionPerformed, true);
//window.addEventListener('keyup', actionPerformed, true);

function sendKey() {
	if (map[38]) { ws.send(JSON.stringify(new Direction("NORTH"))); }
    if (map[39]) { ws.send(JSON.stringify(new Direction("EAST"))); }
    if (map[40]) { ws.send(JSON.stringify(new Direction("SOUTH"))); }
    if (map[37]) { ws.send(JSON.stringify(new Direction("WEST"))); }
}

$(document).keydown(function(e) {
	if (e.keyCode in map && ws !== undefined) {
	    map[e.keyCode] = true;
	}
}).keyup(function(e) {
	if (e.keyCode in map && ws !== undefined) {
	    map[e.keyCode] = false;
	}
});

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

/**********************
 * PARTIE (DE)CONNEXION
 **********************/

/* Crée un nouvel utilisateur dans le serveur */
/* On récupèrera les données suivantes : login, password, firstname, lastname, birthday, email */
/* Ces données sont contenues dans le div d'id = "signin" */
/* On peut donc parcourir le div comme un formulaire avec form[i].value (cf. fichier 'exemple_form') */
function signup() {	
	var labels = $("#signup").children().children();
	var form = function(i) { return labels.get(i).value; }
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
			alert("Votre compte est désormais créé");
			// Réinitialise les champs à vide
			for(var i = 0; i < labels.length; i++){
				labels.get(i).value = "";
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("Erreur lors du chargement du compte: " + errorThrown);
		}
	});
};

/* Identifie un utilisateur dans le serveur et en ressort un joueur */
/* De la même manière, on récupèrera les données du div "signup" */
/* Ce div servant de formulaire contient le login et le password de l'utilisateur */
function signin() {
	var form = function(i) { return $("#signin").children().children().get(i).value; }
	var profil = $("#profil").children();
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
			$("#liSignup").hide();
			$("#liSignin").hide();
			$("#liProfil").show();
			$("#jouer").show();
			profil.get(0).innerHTML = "<h1>" + data.login + "</h1>";
			profil.get(1).innerHTML = data.firstname;
			profil.get(2).innerHTML = data.lastname;
			profil.get(3).innerHTML = data.birthday;
			profil.get(4).innerHTML = data.email;
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("Nom d'utilisateur ou mot de passe incorrect");
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

function connectPlayer(server) {// Wait until the state of the socket is not ready and send the message when it is...

	ws = new WebSocket("ws://" + location.hostname + ":8080/maze/websocket/" + server);

	/* Fonction appelée à l'ouverture d'une websocket */
	ws.onopen = function() {
		console.log("Ouverture de la websocket reussie");
		// Appelle la fonction de vérification des touches appuyées toutes les X secondes
		if (timer !== undefined)
			clearInterval(timer);
		timer = setInterval("sendKey()", 50);
	};
	
	/* Fonction appelée lorsque la websocket est fermée */
	ws.onclose = function() {
		console.log("Fermeture de la websocket reussie");
		// On arrête le timer des touches du clavier
		clearInterval(timer);
		// On met le timer sur le raffraichissement des serveurs
		timer = setInterval("listServers()", 5000);
		// On efface l'appuie des touches du clavier
		for (var p in map) {
			map[p] = false;
		}
		// On n'oublie pas de réinitialiser l'image du labyrinthe
		imaze = undefined;
	};

	/* Fonction appelée en cas d'erreur dans la websocket */
	ws.onerror = function(err) {
		console.log("Une erreur s'est produite dans la websocket")
		console.log(err.data);
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
		if (data.error !== undefined) {
			alert(data.error);
			listServers();
		}
		else if (data.winner !== undefined) {
			alert("Vainqueur: " + data.winner);
			if (ws !== undefined) {
				ws.close();
			}
			$("#jouer").click();
			listServers();
		}
		else if (data.maze !== undefined) {
			drawMaze();
			drawPlayers();
			$("#sam").hide();
			$("canvas").show();
			$("#contentButton").find(":button").hide();
			$(":button#jouer")[0].innerHTML = "Quitter";
			$(":button#jouer").show();
		}
		else {
		 	if (data.slots !== undefined) {
				waitPlayers(data.slots);
			}
			if (data.cellsize !== undefined) {
				SERVER_CELL_SIZE = data.cellsize;
			}
		}
	};
	
	function sendMessage(msg) {
		// Wait until the state of the socket is not ready and send the message when it is...
		waitForSocketConnection(ws, function() {
			ws.send(msg);
		});
	}

	// Make the function wait until the connection is made...
	function waitForSocketConnection(socket, callback) {
		setTimeout(
			function() {
			    if (socket.readyState === 1) {
			        if(callback != null)
			            callback();
			        return;
			    } else
			        waitForSocketConnection(socket, callback);
			}, 5); // wait 5 milisecond for the connection...
	}
	
	sendMessage(JSON.stringify(player));
}
/*************************
 * PARTIE GRAPHIQUE
 *************************/

/* Fonction appelée lorsque l'utilisateur clique avec sa souris */
/* Cette fonction devrait être appelée uniquement si le client demandeur est le maître du labyrinthe (playername === data.master) */
function mouseClicked() {
	//TODO
};

/* Affiche un message d'attente lorsque le nombre minimal de joueurs requis dans une partie n'est pas atteint */
function waitPlayers(slots) {
	
};

/* Dessine le labyrinthe vide en considérant la variable data comme définie (data.maze.cells)*/
/* On préviligiera un parcours à 1 seul indice cells[i] et on récupèrera x et y (cells[i].x et cells[i].y) pour dessiner */
function drawMaze() {
	var canvas = $("canvas")[0];
	var ctx = canvas.getContext("2d");
	var maze = data.maze;
	if (imaze !== undefined) {
		//ctx.fillStyle = "white";
		//ctx.fillRect(0,0,ctx.canvas.width,ctx.canvas.height);
		ctx.drawImage(imaze, 0, 0);
	}
	else {
		var c;
		var cells = maze.cells;
		
		canvas.width = screen.width / 3;
		canvas.height = canvas.width;
		PIXEL_SIZE = canvas.width / maze.width;
		ctx.fillStyle = "white";
		ctx.fillRect(0,0,canvas.width,canvas.height);
		var draw = function(cell,mx,my,px,py) {
					ctx.moveTo(PIXEL_SIZE * cell.x + mx, PIXEL_SIZE * cell.y + my);
					ctx.lineTo(PIXEL_SIZE * cell.x + px, PIXEL_SIZE * cell.y + py);
					ctx.stroke();
				};
	
		// Entrée
		c = maze.start;
		ctx.fillStyle = "rgba(0, 255, 0, .5)";
		ctx.fillRect(PIXEL_SIZE*c.x,PIXEL_SIZE*c.y,PIXEL_SIZE,PIXEL_SIZE);
		ctx.stroke();
	
		// Sortie
		c = maze.exit;
		ctx.fillStyle = "rgba(255, 0, 0, .5)";
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
function drawPlayer(ctx, p) {
	var r = ratio();
	ctx.beginPath();
	ctx.fillStyle = p.color;
	ctx.fill();
	ctx.fillText(p.name, (p.coordinates.x * r)-(p.name.length*12/5), p.coordinates.y*r-5);	
	ctx.fillRect(p.coordinates.x * r, p.coordinates.y * r, r, r);
	ctx.lineWidth = 5;
	ctx.strokeStyle = player.color;
	ctx.stroke();
}

/* Dessine les joueurs dans le labyrinthe */
function drawPlayers() {
	var players = data.players;
	var canvas = $("canvas")[0];
	var ctx = canvas.getContext("2d");
	for (var i=0; i < players.length; i++) {
		drawPlayer(ctx, players[i]);
	}
};

/* Affiche les pièges et bonus que le maître du labyrinthe peut placer (par drag'n'drop de préférence) dans le labyrinthe */
/* Cette fonction devrait être appelée uniquement si le client demandeur est le maître du labyrinthe (playername === data.master) */
function drawItems() {
	//TODO
};


/****************************
 * GESTION D'AFFICHAGE by Sam
 ****************************/
 
function listServers() {
	var lobby = "";
	var ds;
	$.ajax({
		url: "/maze/servers/",
		type: "GET",
		dataType: "json",
		success: function(data, textStatus, jqXHR) {
			for (var i=0; i < data.servers.length; i++) {
				ds = data.servers[i];
				lobby += "<li ondblclick='connectPlayer(\"" + ds.title + "\")'>"
				lobby += "<span style='float:left'>" + ds.title + "</span>"
				lobby += "<span style='float:right; margin-right: 25px'>" + ds.players.length + "/" + ds.slots + ":";
				if (ds.running === false)
					lobby += "en attente de joueurs";
				else if (ds.players.length == ds.slots)
					lobby += "serveur plein";
				else
					lobby += "partie en cours";
				if (ds.players.length === 0) {
					lobby += "<button id='remove' onclick='removeServer(\"" + ds.title + "\")'>X</button>";
				}
				lobby += "</span></li>"
			}
			$("#listeServers").html(lobby);
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("Erreur pendant le chargement des parties: " + errorThrown);
			if (timer !== undefined) {
				clearInterval(timer);
			}
		}
	});
}

function removeServer(server) {
	var lobby = "";
	var ds;
	$.ajax({
		url: "/maze/servers/" + server,
		type: "DELETE",
		success: function(data, textStatus, jqXHR) {
			listServers();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("Erreur lors de la suppression de " + server + ". Erreur: "+ errorThrown);
		}
	});
}
 
// On appuie sur jouer
function jouerPressed() {
	if (ws !== undefined) {
		ws.close();
	}
	if (timer !== undefined) {
		clearInterval(timer);
	}
	timer = setInterval("listServers()", 5000);
	$("#sam").show();
	$("#contentButton").find(":button").show();
	$(":button#jouer").hide();
	$("canvas").hide();
	listServers();
};

// On appuie sur retour
function undoPressed() {
	if (ws !== undefined) {
		ws.close();
	}
	$("#sam").hide();
	$("#contentButton").find(":button").hide();
	$(":button#jouer").show();
	$("#maze").hide();
	$(":button#jouer")[0].innerHTML = "Jouer";
}

function showPlayers() {
	
}

// On appuie sur nouvelle partie
function newGamePressed() {
	$("#contentButton").find(":button").hide();
	$("#contentButton").find("div").show();
	$("#contentButton").find("div").find(":button").show();
}

//On appuie sur annuler dans le menu new game
function cancelServerPressed() {
	$("#contentButton").find(":button").show();
	$(":button#jouer").hide();
	$("#contentButton").find("div").hide();
	$("#contentButton").find("div").find(":button").hide();
}

//On appuie sur créer dans le menu new game
function createServerPressed() {
	var form = function(i) { return $("#contentButton").find("div").find("input").get(i).value; };
	$.ajax({
		url: "/maze/servers/",
		type: "POST",
		data : {
			"title" : form(0),
			"slots" : form(1)
		},
		success: function(data, textStatus, jqXHR) {
			// On raffraichit les serveurs
			listServers();
			$("#contentButton").find("div").hide();
			$("#contentButton").find(":button").show();
			$("#contentButton").find("div").find(":button").hide();
			$(":button#jouer").hide();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("Erreur lors de la création du serveur " + form(0) + ". Code: " + errorThrown);
		}
	
	});
}

function pressEnter(e, fct) {
	if(e.keyCode==13){ fct(); }
}
