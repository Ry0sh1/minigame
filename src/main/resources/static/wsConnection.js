function connect(){
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({},onConnected,onError);
}
function onError(){
    console.log("Error trying to connect to a WebSocket")
}
function onConnected(){
    stompClient.subscribe("/start-game/game/", onMessageReceived);

    stompClient.send("/app/game.join/",
        {},
        JSON.stringify({player: username,content: 'Joined',type: 'JOIN'})
    );
}

function onMessageReceived(payload){
    let message = (JSON.parse(payload.body));
    if (message.type === 'POS'){
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
            fetch("/get-all-player")
                .then(response => response.json())
                .then(data => {
                    for (let i = 0; i < data.length; i++){
                        if (data[i].username === username){
                            player = new Player(0,0,username);
                            players.push(player);
                        }else {
                            players.push(new Player(data[i].x,data[i].y,data[i].username));
                        }
                    }
                })
        }else {
            players.push(new Player(0,0,message.player));
        }
        console.log(players)
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
        bullets.set(bulletAttributes.id, new Bullet(bulletAttributes.id,bulletAttributes.x,bulletAttributes.y,bulletAttributes.dir))
        console.log(bullets)
    }
}

document.getElementById('join-button').addEventListener('click', () => {
    username = document.getElementById('username').value;
    document.querySelector('.user-input').classList.add('hidden');
    canvas.classList.remove('hidden');
    connect();
    requestAnimationFrame(gameLoop);
})
