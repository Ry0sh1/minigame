const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");
let username;

let player;

const players = [];

const keys = {
    w: false,
    a: false,
    s: false,
    d: false
};
const obstacles  = [
    { x: 40, y: 40, width: 60, height: 5 },
    { x: 40, y: 40, width: 5, height: 60 },
    { x: 140, y: 40, width: 5, height: 60 },
    { x: 200, y: 40, width: 5, height: 60 },
    { x: 260, y: 40, width: 5, height: 60 },
    { x: 200, y: 40, width: 5, height: 60 },
    { x: 140, y: 230, width: 100, height: 10 },
    { x: 260, y: 340, width: 50, height: 15 }
]

function gameLoop(){
    requestAnimationFrame(gameLoop);

    update();
    draw();
}

function update() {
    updatePlayerPosition();
}

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = "rgb(255,0,0)";
    players.forEach(p => {
        ctx.fillRect(p.x, p.y, p.width, p.height);
    })

    obstacles.forEach(drawObstacle);
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
    const proposedPosition = { ...player };

    if (keys.w && player.y >0 ) proposedPosition.y -= player.speed;
    if (keys.a && player.x >0 ) proposedPosition.x -= player.speed;
    if (keys.s && player.y < canvas.height -player.height) proposedPosition.y += player.speed;
    if (keys.d && player.x < canvas.width - player.width) proposedPosition.x += player.speed;

    if (!isCollidingWithObstacle(proposedPosition) && (proposedPosition.x !== player.x || proposedPosition.y !== player.y)) {
        stompClient.send("/app/game.pos/",
            {},
            JSON.stringify({type: 'POS', player: player.username,content: proposedPosition.x+','+proposedPosition.y})
        );
    }
}

function isCollidingWithObstacle(proposedPosition) {
    return obstacles.some(obstacle => {
        return !(proposedPosition.x + proposedPosition.width <= obstacle.x ||
            proposedPosition.x >= obstacle.x + obstacle.width ||
            proposedPosition.y + proposedPosition.height <= obstacle.y ||
            proposedPosition.y >= obstacle.y + obstacle.height);
    });
}

function drawObstacle(obstacle) {
    ctx.fillStyle = "rgb(93,120,85)";
    ctx.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);

    ctx.strokeStyle = 'black';
    ctx.lineWidth = 2;
    ctx.strokeRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
}