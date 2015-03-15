var ws = new WebSocket("ws://localhost:8080/");

ws.onopen = function() {
	ws.send("{playername: MisterV}");
	ws.send("{playername: MisterV, direction: EAST}");
};

ws.onmessage = function (evt) {
	var data = JSON.parse(evt.data);
	if (data.motd !== undefined)
		alert(data);
	else
		$("body").innerHTML = evt.data;
};

ws.onclose = function() {
	alert("Closed!");
};

ws.onerror = function(err) {
	alert("Error: " + err);
};

