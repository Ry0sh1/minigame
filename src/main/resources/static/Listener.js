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
    mouseDown = true;
});
canvas.addEventListener('mouseup', (e) => {
    mouseDown = false;
});

canvas.addEventListener('mousemove', (event) => {
    const rect = canvas.getBoundingClientRect();
    mouseX = event.clientX - rect.left;
    mouseY = event.clientY - rect.top;
});

document.getElementById('rifle').addEventListener("click", () => {
    weapon = rifle;
    spawn();
})
document.getElementById('sniper').addEventListener("click", () => {
    weapon = sniper;
    spawn();
})
document.getElementById('shotgun').addEventListener("click", () => {
    weapon = shotgun;
    spawn();
})

function spawn(){
    document.getElementById('change-weapon').classList.add('hidden');
    stompClient.send("/app/game.spawn/" + code,
        {},
        JSON.stringify({type: 'SPAWN', player: username,content: " spawned", code: code})
    );
}