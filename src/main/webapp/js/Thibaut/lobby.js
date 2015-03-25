//var placesRestantes = "0/0";

//var listeBaseLobby = new Array('Fenrir','Leviathan','Alexandre','Phoénix','Marthym','Arkh','Bahamut','Odin','Ifrit','Shiva');

$(document).ready(function(){
	//init(listeBaseLobby);
	//listeServeurs(listeBaseLobby);
});

function animation() {
	var anim = $("#").innerHTML = "<canvas data-processing-sources=" +" animation.js" + "/>";
}

function hide_lobby() {
	$('#liste_lobby').hide();
}

function hide_crea() {
	$('#nouvelle_partie').hide();
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

function init(){
  var listeLobby = "";
  /*for (var i=0; i<listeBaseLobby.length; i++) { 
		listeLobby += "<tr><td>" + listeBaseLobby[i] + "</td><td> places restantes  :  ";
		listeLobby += placesRestantes + "</td><td><button id="+"rejoindre"+" onclick="+"hide_lobby();hide_crea();animation();"+">";
		listeLobby += "Rejoindre</button></td></tr>";
  }
  $("#tab_lobby").append(listeLobby);
  return listeLobby;*/
  
  $.ajax({
		url: "/maze/servers/",
		type: "GET",
		success: function(data, textStatus, jqXHR) {
			document.getElementById("notif").innerHTML = "Recuperation des parties réussie !";
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("notif").innerHTML = "Erreur pendant le chargement des parties: " + errorThrown;
		}
	});
};

function ajoutLobby() {
	var lobby = $("#new_serv_name").val();
	var nb_joueurs = $("#new_serv_num").val();
	
	/*if(nb_joueurs == 1 || nb_joueurs == 0){
		$("#notif").text("Minimum 2 joueurs");
		return;	
	}
	
	if(estDedans(lobby) == false) {
		console.log(estDedans(lobby));
  			var newLobby = "<tr><td>" + lobby + "</td><td> places restantes  :  ";
			newLobby += nb_joueurs + "</td><td><button id="+"rejoindre"+" onclick="+"hide_lobby();hide_crea();animation();"+">";
			newLobby += "Rejoindre</button></td></tr>";
			console.log(newLobby);
  			$("#tab_lobby").append(newLobby);
  			listeBaseLobby.push(lobby);
  			$("#notif").text("Partie créée avec succès !");
  		} else {
  			$("#notif").text("lobby déjà existant !");
  			return;
  		}
  	}*/
	$.ajax({
		url: "/maze/servers/",
		type: "POST",
		data : {
			"title" : lobby,
			"slots" : nb_joueurs,
		},
		success: function(data, textStatus, jqXHR) {
			document.getElementById("notif").innerHTML = "Création de la partie réussie !";
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("notif").innerHTML = "Erreur pendant la création de la partie: " + errorThrown;
		}
	});
}

/*function estDedans(lobby) {
	var lobby = $("#new_serv_name").val();
	for (var i=0; i<listeBaseLobby.length; i++) { 
	console.log(listeBaseLobby[i]);
		if (listeBaseLobby[i] == lobby) {
  			return true;
	} 	
} return false;	
}*/



