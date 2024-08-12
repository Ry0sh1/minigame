function JOIN(message){
    if (message.player === username){
        document.getElementById('game-code').innerText = code;
        const g = JSON.parse(message.content);
        minusSeconds(g.time);
        window.setInterval(() => {gameSec()}, 1000)
        fetch("/get-all-player/" + code)
            .then(response => response.json())
            .then(data => {
                for (let i = 0; i < data.length; i++){
                    if (data[i].username !== username){
                        const p = new Player(data[i].username);
                        p.weapon = getWeaponFromString(data[i].weapon);
                        p.x = data[i].x;
                        p.angle = data[i].angle;
                        p.killCounter = data[i].killCounter;
                        p.deathCounter = data[i].deathCounter;
                        p.alive = data[i].alive;
                        players.set(p.username,p);
                        addPlayerCard(p);
                    }
                }
                player = new Player(username);
                players.set(username, player);
                addPlayerCard(player);
            })
    }else {
        const p = new Player(message.player);
        addPlayerCard(p);
        players.set(p.username, p);
    }
}

function addPlayerCard(p){
    const template = `
        <div class="player-card" id="${p.username}-card">
          <p class="player-card-username" id="player-card-username-${p.username}">${p.username}</p>
          <p>Kills: <span id="player-card-${p.username}-kills">${p.killCounter} </span>Deaths: <span id="player-card-${p.username}-deaths">${p.deathCounter} </span></p>
        </div>`
    document.getElementById('player').insertAdjacentHTML('beforeend', template);
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
function gameSec(){
    minusSeconds(1);
    if (!player.alive && !firstSpawn){
        currentDeathTimer--;
        document.getElementById('respawn-timer').innerText = `${currentDeathTimer}`;
        if (currentDeathTimer <= 0){
            stompClient.send("/app/game.spawn/" + code,
                {},
                JSON.stringify({type: 'SPAWN', player: username,content: player.weapon.name, code: code})
            );
            document.getElementById('death-info').classList.add('hidden');
            document.getElementById('game-main').classList.remove('hidden');
            document.getElementById('change-weapon-button').classList.add('hidden')
            document.getElementById('change-weapon').classList.add('hidden')
        }
    }
    if (currentEvent != null){
        let currentTime = parseInt(document.getElementById('current_event_timer').innerText);
        currentTime--;
        if (currentTime <= 0){
            stopEvent();
            document.getElementById('event_information').classList.add('hidden');
        }else {
            document.getElementById('current_event_timer').innerText = `${currentTime}`;
        }
    }
}