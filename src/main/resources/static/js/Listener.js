document.addEventListener('keydown', (e) => {
    if (e.key in keys) {
        keys[e.key] = true;
    }
});
document.addEventListener('keyup', (e) => {
    if (e.key in keys) {
        keys[e.key] = false;
    }
});
document.addEventListener('keypress', (e) => {
    if (e.key === "e") {
        if (player.currentPowerup != null){
            stompClient.send("/app/game.use-powerup/" + code,
                {},
                JSON.stringify({type: 'USE_POWERUP', player: username,content: player.currentPowerup, code: code})
            );
            player.currentPowerup = null;
            document.getElementById('powerup-display').remove();
        }
    }
});
canvas.addEventListener('mousedown', (e) => {
    if (e.button === 0){
        mouseDown = true;
    }
});
canvas.addEventListener('mouseup', (e) => {
    if (e.button === 0){
        mouseDown = false;
    }
});

canvas.addEventListener('mousemove', (event) => {
    if (player.alive){
        const rect = canvas.getBoundingClientRect();
        mouseX = event.clientX - rect.left;
        mouseY = event.clientY - rect.top;

        let rx = (player.x + player.width / 2) - camera.x;
        let ry = (player.y + player.height / 2) - camera.y;
        player.angle = Math.atan2(mouseY - ry, mouseX - rx);
    }
});

document.getElementById('rifle').addEventListener("click", () => {
    weaponChange(rifle);
})
document.getElementById('sniper').addEventListener("click", () => {
    weaponChange(sniper);
})
document.getElementById('shotgun').addEventListener("click", () => {
    weaponChange(shotgun);
})
document.getElementById('change-weapon-button').addEventListener("click", () => {
    document.getElementById('change-weapon').classList.remove("hidden");
    document.getElementById('change-weapon-button').classList.add("hidden");
})
function weaponChange(weapon){
    player.weapon = weapon;
    document.getElementById('change-weapon').classList.add('hidden');
    if (firstSpawn){
        stompClient.send("/app/game.spawn/" + code,
            {},
            JSON.stringify({type: 'SPAWN', player: username,content: player.weapon.name, code: code})
        );
        document.getElementById('game-main').classList.remove('hidden');
        firstSpawn = false;
    }else {
        document.getElementById('change-weapon-button').classList.remove('hidden');
        stompClient.send("/app/game.change-weapon/" + code,
            {},
            JSON.stringify({type: 'CHANGE_WEAPON', player: username,content: player.weapon.name, code: code})
        );
    }
}