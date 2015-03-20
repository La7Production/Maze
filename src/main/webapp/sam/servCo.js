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


