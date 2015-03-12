/* Une fois les objets json créés selon les touches clavier,
on utilise ws.send(String) pour l'envoyer au serveur.
String = JSON.stringify(monobjetjson)
*/

var ws = new WebSocket("ws://127.0.0.1:9876/");
var login;

ws.onopen = function() {
	alert("Opened!");
	ws.send("Hello Server i'm a client");
};

ws.onmessage = function (evt) {
	console.log(JSON.parse(evt.data));
};

ws.onclose = function() {
	alert("Closed!");
};

ws.onerror = function(err) {
	alert("Error: " + err);
};


function hideMenuCrea() {
	$('#creerCompte').hide();
}

function showMenuCrea() {
	$('#creerCompte').show();
}

function hideMenuCo() {
	$('#connexion').hide();
}

function showMenuCo() {
	$('#connexion').show();
}

function paintCanvas(){
	var c = document.getElementById("exemple");
	var ctx = c.getContext("2d");
	
	for(var i = 0; i < 10; i++){
		for(var j = 0; j < 10; j++){
			if((i+j)%2 === 0) {
				ctx.fillStyle = "rgba(0,0,128,128)";
   			ctx.fillRect(i*50, j*50, 50, 50);
			} else {
				ctx.fillStyle = "rgba(0,0,255,0.5)";
   			ctx.fillRect(i*50, 50*j, 50, 50);
			}
					
		}		    	
	}
}

function creaCompte(){
	$('#creer').click(function(event) {
			$.ajax({
			 
				url: "url du serveur",
				type: "POST",
				dataType: "json",
				
				data : {
					'pseudo' : $('#pseudo').val(),
					'eMail' : $('#eMail').val(),
					'mdp' : $('#mdp').val(),
				},
				
				success: function( json ) {
					alert("création du compte : " + json);
				},
			});
		});
}

function coCompte() {
	login = $('#pseudoCo').val();
	/*$('#co').click(function (event) {
		$.ajax({
				url: "url du serveur",
				type: "GET"	,
				dataType: "json",
				
				data : {
					'pseudoCo' : $('#pseudoCo').val(),
					'mdp' : $('#mdp').val(),				
				},
				
				success: function (json) {
					alert("recuperation du compte :" + json);
				},
						
			});
	});*/
}

function deplacementJoueur(event) {
	var envoi;
	if(event.keyCode===37){
		envoi={
				login:login,
				direction:"ouest",
			};
	} else if(event.keyCode===38){
		envoi={
				login:login,
				direction:"nord",
			};
	} else if(event.keyCode===39){
		envoi={
				login:login,
				direction:"est",
			};
	} else if(event.keyCode===40){
		envoi={
				login:login,
				direction:"sud",
			};
	}	
	
	console.log(envoi);
	ws.send(JSON.stringify(envoi));
	
	/*$.ajax({
				url: "url du serveur",
				type: "PUT"	,
				dataType: "json",
				
				data : envoi,
				
				success: function (json) {
					alert("Appuie sur la touche :" + json);
				},
						
			});*/
}



