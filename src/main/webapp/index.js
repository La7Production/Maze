var ws = new WebSocket("ws://localhost:8080/maze");

ws.onopen = function() {
	alert("Opened!");
	ws.send("Hello Server i'm a client");
};

ws.onmessage = function (evt) {
	alert(evt.data);
};

ws.onclose = function() {
	alert("Closed!");
};

ws.onerror = function(err) {
	alert("Error: " + err);
};

