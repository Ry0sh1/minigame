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
    const rect = canvas.getBoundingClientRect();
    mouseX = event.clientX - rect.left;
    mouseY = event.clientY - rect.top;

    let rx = (player.x + player.width / 2) - camera.x;
    let ry = (player.y + player.height / 2) - camera.y;
    let angle = Math.atan2(mouseY - ry, mouseX - rx);

    stompClient.send("/app/game.view-angle/" + code,
        {},
        JSON.stringify({type: 'VIEW_ANGLE', player: username,content: angle, code: code})
    );
});

document.getElementById('rifle').addEventListener("click", () => {
    spawn('rifle');
})
document.getElementById('sniper').addEventListener("click", () => {
    spawn('sniper');
})
document.getElementById('shotgun').addEventListener("click", () => {
    spawn('shotgun');
})

function spawn(weapon){
    document.getElementById('change-weapon').classList.add('hidden');
    stompClient.send("/app/game.spawn/" + code,
        {},
        JSON.stringify({type: 'SPAWN', player: username,content: weapon, code: code})
    );
}