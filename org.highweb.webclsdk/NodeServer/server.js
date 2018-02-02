var static = require('node-static');
var http = require('http');
const url = require('url');
var file = new(static.Server)();
var app = http.createServer(function (req, res) {
  file.serve(req, res);
}).listen(2018);

console.log("Server Start!");

var io = require('socket.io').listen(app);
const descriptions = {offer : [], answer: []};
io.sockets.on('connection', function (socket){
	// convenience function to log server messages on the client

	socket.on('message', function (room, message) {
    	// for a real app, would be room only (not broadcast)
		io.sockets.in(room).emit('message', message);
	});

	socket.on('info', function (message){
		io.sockets.in('server').emit('info', message);
	});

	socket.on('end', function (){
		io.sockets.in('server').emit('end');
	});

	socket.on('sended', function(data){
		io.sockets.in('HIGHWEB').emit('sended', data);
	});

	socket.on('create or join', function (room) {
		//socket.io version up후 다음과 같이 바뀜
		//var numClients = io.sockets.clients(room).length;
		let rooms = io.sockets.adapter.rooms[room];
		let numClients = rooms? rooms.length : 0;

		if (numClients === 0){
			socket.join(room);
			socket.emit('created', room);
		} else if (numClients === 1) {
			io.sockets.in(room).emit('join', room);
			socket.join(room);
			socket.emit('joined', room);
		} else { // max two clients
			socket.emit('full', room);
		}
	});
});