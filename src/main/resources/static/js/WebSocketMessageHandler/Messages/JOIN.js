function JOIN(message){
    if (message.player === username){
        document.getElementById('game-code').innerText = code;
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