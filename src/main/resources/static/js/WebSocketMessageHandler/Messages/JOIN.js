function JOIN(message){
    if (message.player === username){
        document.getElementById('game-code').innerText = code;
        const g = JSON.parse(message.content);
        fetch("/get-all-player/" + code)
            .then(response => response.json())
            .then(data => {
                for (let i = 0; i < data.length; i++){
                    if (data[i].username !== username){
                        const p = new Player(data[i].username);
                        p.weapon = getWeaponFromString(data[i].weapon);
                        p.x = data[i].x;
                        p.y = data[i].y;
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
          <p class="player-card-username" id="player-card-username-${p.username}">${p.username} </p>
          <p>Kills: <span id="player-card-${p.username}-kills">${p.killCounter} </span>Deaths: <span id="player-card-${p.username}-deaths">${p.deathCounter} </span></p>
        </div>`
    document.getElementById('player').insertAdjacentHTML('beforeend', template);
    if (p.username === username){
        document.getElementById(`${p.username}-card`).classList.add('me');
        let span = document.createElement('span');
        span.innerText = " (You)";
        document.getElementById(`player-card-username-${p.username}`).append(span);
    }
}