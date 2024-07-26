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

function gameLoop(){
    requestAnimationFrame(gameLoop);

    update();
    draw();
}

function update() {
    player.updatePlayerPosition();
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
