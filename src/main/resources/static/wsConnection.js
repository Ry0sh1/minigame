function connect(){
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({},onConnected,onError);
}
function onError(){
    console.log("Error trying to connect to a WebSocket")
}
function onConnected(){
    stompClient.subscribe("/start-game/game/" + code, onMessageReceived);

    stompClient.send("/app/game.join/" + code,
        {},
        JSON.stringify({player: username,content: 'Joined',type: 'JOIN', code: code})
    );
}

function onMessageReceived(payload){
    let message = (JSON.parse(payload.body));
    if (message.type === 'POSITION'){
        let pos = message.content.split(',');
        if (players.has(message.player)){
            const p = players.get(message.player);
            p.x = parseInt(pos[0]);
            p.y = parseInt(pos[1]);
        }
    }
    if (message.type === 'JOIN'){
        if (message.player === username){
            fetch("/get-all-player/" + code)
                .then(response => response.json())
                .then(data => {
                    for (let i = 0; i < data.length; i++){
                        if (data[i].username !== username){
                            players.set(data[i].username, new Player(data[i].x,data[i].y,data[i].username));
                        }
                    }
                })
        }
    }
    if (message.type === 'LEFT'){
        if (players.has(message.player)) players.delete(message.player);
    }
    if (message.type === 'SHOOT'){
        let bulletAttributes = (JSON.parse(message.content));
        if (message.player === player.username){
            playerBullets.set(bulletAttributes.id, new Bullet(bulletAttributes.id,bulletAttributes.x,bulletAttributes.y,bulletAttributes.angle,bulletAttributes.speed))
        }
        else{
            bullets.set(bulletAttributes.id, new Bullet(bulletAttributes.id,bulletAttributes.x,bulletAttributes.y,bulletAttributes.angle,bulletAttributes.speed))
        }
    }
    if (message.type === 'DELETE_BULLET'){
        if (bullets.has(message.content)) bullets.delete(message.content);
        if (playerBullets.has(message.content)) playerBullets.delete(message.content);
    }
    if (message.type === 'SPAWN'){
        if (message.player === username){
            player = new Player(0,0,username);
            players.set(username, player);
            camera = new Camera(0,0, canvas.width, canvas.height)
            alive = true;
        }else {
            players.set(message.player, new Player(0,0, message.player));
        }
    }
    if (message.type === 'PLAYER_HIT'){
        if (message.content === username){
            alive = false;
            document.getElementById('change-weapon').classList.remove('hidden');
            player = null;
            camera = null;
        }
        if (players.has(message.content)) players.delete(message.content);
    }
}

connect();
requestAnimationFrame(gameLoop)