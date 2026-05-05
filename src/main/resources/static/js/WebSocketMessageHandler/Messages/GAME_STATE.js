function GAME_STATE(gameState) {
    players = gameState.players;
    bullets = gameState.bullets;
    heals = gameState.heals;
    powerUps = gameState.powerUps;

    redrawPlayerCards();

    gameState.players.forEach(p => {
        if (p.username === username) {
            player = p;

            if (player.weapon === null) return;


            document.getElementById('hp').innerText = player.hp;

            if (player.alive) {
                camera = new Camera(0,0, canvas.width, canvas.height);
                document.getElementById('change-weapon-button').classList.add('hidden');
                document.getElementById('change-weapon').classList.add('hidden');
                document.getElementById('death-info').classList.add('hidden');
                document.getElementById('game-main').classList.remove('hidden');
                document.getElementById('change-weapon-button').classList.add('hidden');
                document.getElementById('change-weapon').classList.add('hidden');

                camera.follow(player);
                draw();
                drawMiniMap();
            } else {
                camera = null;
                document.getElementById('change-weapon-button').classList.remove('hidden');
                document.getElementById('death-info').classList.remove('hidden');
                document.getElementById('game-main').classList.add('hidden');
                document.getElementById('respawn-timer').innerText = player.respawnTimer;
            }
        }
    });
}

function redrawPlayerCards() {
    document.getElementById('player').remove();

    let playerDiv = document.createElement('div');
    playerDiv.id = 'player';

    document.getElementById('game-view').insertAdjacentElement('afterbegin', playerDiv);

    players.forEach(p => {
        addPlayerCard(p)
    });
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