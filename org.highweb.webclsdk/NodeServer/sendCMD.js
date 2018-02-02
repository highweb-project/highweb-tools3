const io = require('socket.io-client');
const socket = io(`http://${process.argv[2]}`, {timeout : 5000});

socket.on('connect_timeout', (timeout) => {
    console.log("Time Out!");
    process.exit();
  });

socket.on('end', ()=>{
    process.exit();
});

socket.on('info', (message)=>{
    console.log(message);
});

socket.emit('create or join', 'server');
socket.emit('sended', process.argv[3]);
