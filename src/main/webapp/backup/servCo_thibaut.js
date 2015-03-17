/* Une fois les objets json créés selon les touches clavier,
on utilise ws.send(String) pour l'envoyer au serveur.
String = JSON.stringify(monobjetjson)
*/

var ws = new WebSocket("ws://127.0.0.1:9876/");
var login;
var maze = 1;
var canvasJeu = Create2DArray(10);


canvas.style.left="500";

canvas.style.top="110";
canvas.style.border="5px solid black";

function Create2DArray(rows){
	var arr = new Array(rows);
	
	for(var i = 0; i < rows; i++){
		arr[i] = new Array(rows);
	}
	return arr;
}

canvas.addEventListener("mousedown", mouseClicked, false);

ws.onopen = function() {
	alert("Opened!");
	ws.send("Hello Server i'm a client");
};

ws.onmessage = function (evt) {
	var ligne;
	var colonne;
	var valeur;
	var cellsTab=[];
	var sizeTab = (canvas.height/10);
	console.log(maze);
	maze=JSON.parse(evt.data);
	console.log(maze);
	var c = document.getElementById("canvas");
	var ctx = c.getContext("2d");
	for(ligne = 0; ligne < 10; ligne++){
		for(colonne = 0; colonne < 10; colonne++){	
			cellsTab[ligne, colonne] = valeur;
			ctx.strokeRect(ligne*sizeTab,colonne*sizeTab,sizeTab,sizeTab);
			ctx.strokeStyle="red";
		}
	}	
	
	
	/*for(var i=0; i<canvas.height; i++){
		for(var j=0;j<canvas.width;j++) {
			console.log(cellsTab[i,j].valeur);
			nb = parseInt(cellsTab[i, j]);
			if ((nb & 1) === 0) {
				td.style.borderTop = "1px solid black";
			}
			if ((nb & 2) === 0) {
				td.style.borderRight = "1px solid black";
			}
			if ((nb & 4) === 0) {
				td.style.borderBottom = "1px solid black";
			}
			if ((nb & 8) === 0) {
				td.style.borderLeft = "1px solid black";
			}
		}
	}*/
	
	//nb=parseInt();
    /*var table = $('<table border=1>');
    var tableHeader;
	 for(var i=0; i<maze.height; i++){
		tableHeader += "<tr>" + i + "</tr>";
		for(var j=0;j<maze.width;j++) {
			tableHeader += "<td id=" + j + "-" + i + ">" + maze.cells[i*maze.width+j].value +"</td>";
		}
	}
	
	$("#canvas").append(tableHeader);
	
	 for(var i=0; i<maze.height; i++){
		for(var j=0;j<maze.width;j++) {
			td = document.getElementById(j + "-" + i);
			nb = parseInt(td.innerHTML);
			if ((nb & 1) === 0) {
				td.style.borderTop = "1px solid black";
			}
			if ((nb & 2) === 0) {
				td.style.borderRight = "1px solid black";
			}
			if ((nb & 4) === 0) {
				td.style.borderBottom = "1px solid black";
			}
			if ((nb & 8) === 0) {
				td.style.borderLeft = "1px solid black";
			}
		}
		
	}*/
	
	
};

var convertCoordinates = function(ligne, colonne) {
    var lig = Math.ceil(ligne / (canvas.height/10)) - 1;
    var col = Math.ceil(colonne / (canvas.width/10)) - 1;
    return [lig, col];
}

//var mouseClicked = function(event) { // Pourquoi cela ne fonctionne pas avec var ?!
function mouseClicked(event,maze) {
	 console.log(maze);
    var ligCoord = event.pageY - canvas.offsetTop;
    var colCoord = event.pageX - canvas.offsetLeft;
    var valeurCase = maze.cells[ligCoord,colCoord].value;
    var coord    = convertCoordinates(ligCoord, colCoord, valeurCase);
    console.info(coord);
 }
 

    
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
	$('#co').click(function (event) {
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
	});
}

function deplacementJoueur(event) {
	var envoi;
	if(event.keyCode===37){
		envoi={
				login:login,
				direction:"WEST",
			};
	} else if(event.keyCode===38){
		envoi={
				login:login,
				direction:"NORTH",
			};
	} else if(event.keyCode===39){
		envoi={
				login:login,
				direction:"EAST",
			};
	} else if(event.keyCode===40){
		envoi={
				login:login,
				direction:"SOUTH",
			};
	}	
	ws.send(JSON.stringify(envoi));
}


