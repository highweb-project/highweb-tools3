const log = (_=> {
  const consoleTextArea = document.getElementById('console');
  const log = arg => consoleTextArea.value += `[${new Date().toISOString().substring(11, 19)}]${arg}\n`;
  return {
      error: arg => log(`[ERROR] ${arg}`),
      info: arg => log(`[INFO] ${arg}`)
  }
})();

const socket = io.connect();

socket.on('created', room=>{
  log.info(`Crated '${room}' room`);
});

socket.on('join', room=>{
  log.info(`Someone joined '${room}' room`);
});

socket.on('joined', room=>{
  log.info(`Joined '${room}' room`);
});
