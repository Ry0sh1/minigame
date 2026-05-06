document.addEventListener('keydown', (e) => {
    if (keys.includes(e.key)) {
        stompClient.send("/app/game.move",
            {},
            JSON.stringify({type: 'MOVE', player: username, content: e.key, code: code})
        );
    }
});
document.addEventListener('keyup', (e) => {
    if (keys.includes(e.key)) {
        stompClient.send("/app/game.stop-move",
            {},
            JSON.stringify({type: 'STOP_MOVE', player: username, content: e.key, code: code})
        );
    }
});
document.addEventListener('keypress', (e) => {
    if (e.key === "e") {
        stompClient.send("/app/game.use-powerup",
            {},
            JSON.stringify({type: 'USE_POWERUP', player: username, content: player.currentPowerUp, code: code})
        );
    }
});
canvas.addEventListener('mousedown', (e) => {
    if (e.button === 0){
        stompClient.send("/app/game.shoot",
            {},
            JSON.stringify({type: 'SHOOT', player: username, content: true, code: code})
        );
    }
});
canvas.addEventListener('mouseup', (e) => {
    if (e.button === 0){
        stompClient.send("/app/game.shoot",
            {},
            JSON.stringify({type: 'SHOOT', player: username, content: false, code: code})
        );
    }
});

canvas.addEventListener('mousemove', (event) => {
    const rect = canvas.getBoundingClientRect();
    mouseX = event.clientX - rect.left;
    mouseY = event.clientY - rect.top;

    let rx = (player.x + player.width / 2) - camera.x;
    let ry = (player.y + player.height / 2) - camera.y;
    player.angle = Math.atan2(mouseY - ry, mouseX - rx);
    stompClient.send("/app/game.view-angle",
        {},
        JSON.stringify({type: 'VIEW_ANGLE', player: username, content: player.angle, code: code})
    );
});

document.getElementById('change-weapon-button').addEventListener("click", () => {
    document.getElementById('change-weapon').classList.remove("hidden");
    document.getElementById('change-weapon-button').classList.add("hidden");
})
function weaponChange(weaponName){
    document.getElementById('change-weapon').classList.add('hidden');
    document.getElementById('change-weapon-button').classList.remove('hidden');
    stompClient.send("/app/game.change-weapon",
        {},
        JSON.stringify({type: 'CHANGE_WEAPON', player: username,content: weaponName, code: code})
    );
}