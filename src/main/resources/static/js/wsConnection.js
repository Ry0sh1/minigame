// Speichern der Original-Konsole
const originalConsoleLog = console.log;

// Temporäres Umleiten von console.log
console.log = function(message) {
    if (typeof message === 'string' && (message.includes('destination:/start-game/game/') || message.includes('destination:/app/'))) {
        // Unterdrücke Nachrichten, die die spezifische Destination enthalten
        return;
    }
    originalConsoleLog.apply(console, arguments);
};

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
            const g = JSON.parse(message.content);
            minusSeconds(g.time);
            window.setInterval(() => {gameSec()}, 1000)
            fetch("/get-all-player/" + code)
                .then(response => response.json())
                .then(data => {
                    for (let i = 0; i < data.length; i++){
                        if (data[i].username !== username){
                            players.set(data[i].username, new Player(parseInt(data[i].x),parseInt(data[i].y),data[i].username, data[i].angle, getWeaponFromString(data[i].weapon)));
                        }
                    }
                    player = new Player(-1000, -1000, username, 0, rifle);
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
        const content = JSON.parse(message.content);
        if (message.player === username){
            player.x = parseInt(content.x);
            player.y = parseInt(content.y);
            player.angle = 0;
            player.weapon = getWeaponFromString(content.weapon);
            players.set(username, player);
            camera = new Camera(0,0, canvas.width, canvas.height);
            document.getElementById('hp').innerText = player.hp;
            alive = true;
        }else {
            players.set(message.player, new Player(content.x,content.y, message.player, 0, getWeaponFromString(content.weapon)));
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
            alive = false;
            camera = null;
            document.getElementById('change-weapon-button').classList.remove('hidden');
            document.getElementById('respawn-timer').classList.remove('hidden');
            currentDeathTimer = settings.respawnTimer;
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
        if (message.content === 'Darkness'){
            currentEvent = message.content;
            player.nearsight = true;
            document.getElementById('current_event').innerText = "Darkness!"
            document.getElementById('event_information').classList.remove('hidden');
            document.getElementById('current_event_timer').innerText = '30';
        }
        //TODO: Each Event
    }
    if (message.type === 'END_GAME'){
        //TODO: End Screen
    }
    if (message.type === 'VIEW_ANGLE'){
        const p = players.get(message.player);
        p.angle = parseFloat(message.content);
        players.set(message.player, p);
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

function stopEvent(){
    if (currentEvent === 'Darkness'){
        player.nearsight = false;
    }
}

function gameSec(){
    minusSeconds(1);
    if (!alive && !firstSpawn){
        currentDeathTimer--;
        document.getElementById('respawn-timer').innerText = `${currentDeathTimer}`;
        if (currentDeathTimer <= 0){
            stompClient.send("/app/game.spawn/" + code,
                {},
                JSON.stringify({type: 'SPAWN', player: username,content: player.weapon.name, code: code})
            );
            document.getElementById('respawn-timer').classList.add('hidden');
        }
    }
    if (currentEvent != null){
        let currentTime = document.getElementById('current_event_timer').innerText;
        currentTime--;
        if (currentTime <= 0){
            stopEvent();
            document.getElementById('event_information').classList.add('hidden');
        }else {
            document.getElementById('current_event_timer').innerText = currentTime;
        }
    }
}
function getWeaponFromString(weaponString){
    switch (weaponString){
        case 'shotgun': return shotgun;
        case 'sniper': return sniper;
        case 'rifle': return rifle;
    }
}
fetch("/get-map-data/" + code, {method: 'GET'})
    .then(response => response.json())
    .then(data => {
        map = data;
        for (let i = 0; i < map.heal_pads.length; i++){
            heal.set(map.heal_pads[i].id,new Heal(map.heal_pads[i].id,map.heal_pads[i].x,map.heal_pads[i].y,settings.healHitBoxWidth,settings.healHitBoxHeight));
        }
        connect();
        requestAnimationFrame(gameLoop)
    })
