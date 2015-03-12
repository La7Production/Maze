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

function creaCompte() {
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
	$('#co').click(function (event) {
		$.ajax({
				url: "url du serveur",
				type: "GET"	,
				dataType: "json",
				
				data : {
					'pseudo' : $('#pseudo').val(),
					'mdp' : $('#mdp').val(),				
				},
				
				success: function (json) {
					alert("recuperation du compte :" + json);
				},
						
			});
	});
}

function deplacementJoueur(login) {
	
}

function paintCanvas(){
	var c = document.getElementById("exemple");
	var ctx = c.getContext("2d");
	
	for(var i = 0; i < 5; i++){
		for(var j = 0; j < 5; j++){
			if((i+j)%2 === 0) {
				ctx.fillStyle = "rgba(0,0,128,128)";
   			ctx.fillRect(i*100, j*100, 100, 100);
			} else {
				ctx.fillStyle = "rgba(0,0,255,0.5)";
   			ctx.fillRect(i*100, 100*j, 100, 100);
			}
					
		   }		    	
		}
	}

