var ws = new WebSocket("ws://127.0.0.1:8080/");
var maze;

ws.onopen = function() {
	alert("Opened!");
	ws.send("Hello Server i'm a client");
};

ws.onmessage = function (evt) {
	maze = JSON.parse(evt.data);
	console.log(maze);
};

ws.onclose = function() {
	alert("Closed!");
};

ws.onerror = function(err) {
	alert("Error: " + err);
};

$("#submit").click(function() {
	alert($("#tosend").val());
	ws.send($("#tosend").val());
});

