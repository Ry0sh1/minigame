function GAME_STATE(gameState) {
    players = gameState.players;
    bullets = gameState.bullets;
    heals = gameState.heals;
    powerUps = gameState.powerUps;

    gameState.players.forEach(p => {
        if (p.username === username) {
            player = p;
        }
    });

    draw();
    redrawPlayerCards();
    drawMiniMap();
    camera.follow(player);
}

function redrawPlayerCards() {
    players.forEach(p => {
        document.getElementById('player-card-' + p.username + "-kills").innerText = `${p.killCounter}`;
        document.getElementById('player-card-' + p.username + "-deaths").innerText = `${p.deathCounter}`;
    });
}