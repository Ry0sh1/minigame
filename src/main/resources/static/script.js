const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");
const mapWidth = 2000;
const mapHeight = 2000;

let username;
let player;
let camera;

let mouseX = 0;
let mouseY = 0;

const players = [];

const keys = {
    w: false,
    a: false,
    s: false,
    d: false,
};

const bullets = new Map();

const obstacles  = [
    { x: 40, y: 40, width: 60, height: 5 },
    { x: 40, y: 40, width: 5, height: 60 },
    { x: 140, y: 40, width: 5, height: 60 },
    { x: 200, y: 40, width: 5, height: 60 },
    { x: 430, y: 40, width: 5, height: 60 },
    { x: 243, y: 350, width: 5, height: 60 },
    { x: 754, y: 930, width: 100, height: 10 },
    { x: 854, y: 530, width: 50, height: 15 }
]

function gameLoop(){
    requestAnimationFrame(gameLoop);

    update();
    draw();
}

function update() {
    updatePlayerPosition();
    camera.follow(player);
    for (let [key,value] of bullets){
        value.move();
    }
}

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = "rgb(255,0,0)";
    players.forEach(p => {
        ctx.fillRect(p.x - camera.x, p.y - camera.y, p.width, p.height);
    })

    ctx.fillStyle ="rgb(18,116,2)";
    for (let [key,value] of bullets){
        ctx.beginPath();
        ctx.arc(value.x - camera.x, value.y - camera.y, value.radius, 0, Math.PI * 2);
        ctx.fill();
    }

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
canvas.addEventListener('mousedown', (e) => {
    shoot();
});

canvas.addEventListener('mousemove', (event) => {
    const rect = canvas.getBoundingClientRect();
    mouseX = event.clientX - rect.left;
    mouseY = event.clientY - rect.top;
});

function updatePlayerPosition() {
    const proposedPosition = { ...player };

    if (keys.w && player.y >0 ) proposedPosition.y -= player.speed;
    if (keys.a && player.x >0 ) proposedPosition.x -= player.speed;
    if (keys.s && player.y < mapHeight -player.height) proposedPosition.y += player.speed;
    if (keys.d && player.x < mapWidth - player.width) proposedPosition.x += player.speed;

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
    ctx.fillRect(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.width, obstacle.height);

    ctx.strokeStyle = 'black';
    ctx.lineWidth = 2;
    ctx.strokeRect(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.width, obstacle.height);
}

function shoot(){
    let bulX = (player.x + player.width / 2) - camera.x;
    let bulY  = (player.y + player.height / 2) - camera.y;
    const angle = Math.atan2(mouseY - bulY, mouseX - bulX);
    stompClient.send("/app/game.shoot/",
        {},
        JSON.stringify({type: 'SHOOT', player: player.username,content: angle})
    );
}
