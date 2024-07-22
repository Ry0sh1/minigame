const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");

const player = {
    x: 50,
    y: 50,
    width: 20,
    height: 20,
    speed: 3,
}

const keys = {
    w: false,
    a: false,
    s: false,
    d: false
};

function gameLoop(){
    requestAnimationFrame(gameLoop);

    update();
    draw();
    updatePlayerPosition();
}

function update() {

}

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = "rgb(255,0,0)";
    ctx.fillRect(player.x, player.y, player.width, player.height);
}

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

function updatePlayerPosition() {
    if (keys.w && player.y >0 ) player.y -= player.speed;
    if (keys.a && player.x >0 ) player.x -= player.speed;
    if (keys.s && player.y < canvas.height -player.height) player.y += player.speed;
    if (keys.d && player.x < canvas.width - player.width) player.x += player.speed;
}

requestAnimationFrame(gameLoop);