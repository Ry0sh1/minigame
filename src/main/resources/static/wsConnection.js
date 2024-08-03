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
            fetch("/get-game/" + code)
                .then(response => response.json())
                .then(data => {
                    minusSeconds(data.time)
                    window.setInterval(() => {
                        minusSeconds(1);
                    }, 1000)
                })
            fetch("/get-all-player/" + code)
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    for (let i = 0; i < data.length; i++){
                        if (data[i].username !== username){
                            players.set(data[i].username, new Player(parseInt(data[i].x),parseInt(data[i].y),data[i].username));
                        }
                    }
                    console.log(players)
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
            const content = JSON.parse(message.content);
            player = new Player(parseInt(content.x),parseInt(content.y),content.username);
            players.set(username, player);
            camera = new Camera(0,0, canvas.width, canvas.height)
            alive = true;
            document.getElementById('hp').innerText = player.hp;
        }else {
            players.set(message.player, new Player(0,0, message.player));
        }
    }
    if (message.type === 'PLAYER_HIT'){
        if (message.content.split(",")[0] === username){
            player.hp = player.hp - parseInt(message.content.split(",")[1]);
            document.getElementById('hp').innerText = player.hp;
        }
    }
    if (message.type === 'KILLED'){
        if (message.content === username){
            player.hp = 0;
            document.getElementById('hp').innerText = player.hp;
            document.getElementById('change-weapon').classList.remove('hidden');
            alive = false;
            player = null;
            camera = null;
        }
        if (players.has(message.content)) players.delete(message.content);
    }
    if (message.type === 'HEAL'){
        const healID = parseInt(message.content);
        if (heal.has(healID)){
            const h = heal.get(healID);
            h.active = false;
            heal.set(healID, h);
            if (player.hp + settings.heal <= 100){
                player.hp += settings.heal;
            }else {
                player.hp = 100;
            }
            document.getElementById('hp').innerText = `${player.hp}`;
        }
    }
    if (message.type === 'REACTIVATE_HEAL'){
        const healID = parseInt(message.content);
        if (heal.has(healID)){
            const h = heal.get(healID);
            h.active = true;
            heal.set(healID, h);
        }
    }
    if (message.type === 'EVENT'){
        //TODO: Each Event
    }
    if (message.type === 'END_GAME'){
        alive = false;
        //TODO: End Screen
    }
}

function minusSeconds(seconds){
    let timeArr = document.getElementById("game-timer").innerText.split(":");
    let timeInSeconds = parseInt(timeArr[0])*60 + parseInt(timeArr[1]);
    let min = Math.floor((timeInSeconds - seconds) / 60);
    let sec = (timeInSeconds - seconds) % 60;
    if (sec < 10){
        sec = `0${sec}`;
    }
    document.getElementById("game-timer").innerText = `0${min}:${sec}`;
}

fetch("/get-map-data/" + code, {method: 'GET'})
    .then(response => response.json())
    .then(data => {
        map = data;
        connect();
        requestAnimationFrame(gameLoop)
    })