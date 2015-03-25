var placesRestantes = "0/0";

var listeBaseLobby = new Array('Fenrir','Leviathan','Alexandre','Phoénix','Marthym','Arkh','Bahamut','Odin','Ifrit','Shiva');

$(document).ready(function(){
	init(listeBaseLobby);
});

function hide_lobby() {
	$('#liste_lobby').hide();
}

function hide_crea() {
	$('#crea_partie').hide();
}

function show_lobby() {
	$('#liste_lobby').show();
}

function show_crea() {
	$('#crea_partie').show();
}

function show_new_serv() {
	$('#nouveau_nom').show();
}

function hide_new_serv() {
	$('#nouveau_nom').hide();
}

function init(listeBaseLobby){
  var listeLobby = "";
  for (var i=0; i<listeBaseLobby.length; i++) { 
		listeLobby += "<tr><td>" + listeBaseLobby[i] + "</td><td> places restantes  :  ";
		listeLobby += placesRestantes + "</td><td><button id="+"rejoindre"+" onclick="+"hide_lobby();hide_crea();"+">";
		listeLobby += "Rejoindre</button></td></tr>";
  }
  $("#tab_lobby").append(listeLobby);
  console.log(listeLobby);
  return listeLobby;
};

function ajoutLobby(lobby) {
	var newLobby = "<tr><td>" + lobby + ":" + placesRestantes + "</td></tr>"
	newLobby.appendTo("#tab_lobby");
}

function nbrJoueursLobby(lobby) {
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
			document.getElementById("notif").innerHTML = "Recuperation des lobby réussie !";
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("notif").innerHTML = "Erreur pendant le chargement des lobby: " + errorThrown;
		}
	});
}