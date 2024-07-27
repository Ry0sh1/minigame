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
        players.forEach(p => {
            if (p.username === message.player){
                p.x = parseInt(pos[0]);
                p.y = parseInt(pos[1]);
            }
        })
    }
    if (message.type === 'JOIN'){
        if (message.player === username){
            fetch("/get-all-player/" + code)
                .then(response => response.json())
                .then(data => {
                    for (let i = 0; i < data.length; i++){
                        console.log(data[i].username === username)
                        if (data[i].username === username){
                            player = new Player(0,0,username);
                            players.push(player);
                            camera = new Camera(0,0, canvas.width, canvas.height)
                        }else {
                            players.push(new Player(data[i].x,data[i].y,data[i].username));
                        }
                    }
                })
        }else {
            players.push(new Player(0,0,message.player));
        }
    }
    if (message.type === 'LEFT'){
        for (let i = 0; i < players.length; i++){
            if (message.player === players[i].username){
                players.splice(i,1);
            }
        }
    }
    if (message.type === 'SHOOT'){
        let bulletAttributes = (JSON.parse(message.content));
        if (message.player === player.username){
            playerBullets.set(bulletAttributes.id, new Bullet(bulletAttributes.id,bulletAttributes.x,bulletAttributes.y,bulletAttributes.angle))
        }
        else{
            bullets.set(bulletAttributes.id, new Bullet(bulletAttributes.id,bulletAttributes.x,bulletAttributes.y,bulletAttributes.angle))
        }
    }
    if (message.type === 'DELETE_BULLET'){
        if (bullets.has(message.content)) bullets.delete(message.content);
        if (playerBullets.has(message.content)) playerBullets.delete(message.content);
    }
}

connect();
requestAnimationFrame(gameLoop)