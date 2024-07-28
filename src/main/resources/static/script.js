const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");
const playerVisionAngle = 25; //Winkel für das Sichtfeld
const playerVisionRadius = 40; //Radius von dem Kreis der Vision
const shootRadius = 10 + 5;
const mapWidth = 2000;
const mapHeight = 2000;

const code = localStorage.getItem("code");
let username = localStorage.getItem("username");
let player;
let weapon;
let camera;
let alive = false;

let mouseX = 0;
let mouseY = 0;

const players = new Map();

const keys = {
    w: false,
    a: false,
    s: false,
    d: false,
};
let mouseDown = false;

const bullets = new Map();
const playerBullets = new Map();

let lastTime = 0;
const fps = 60;
const fpsInterval = 1000 / fps;

function gameLoop(currentTime){
    requestAnimationFrame(gameLoop);
    if (alive){

        const elapsed = currentTime - lastTime;

        if (elapsed > fpsInterval) {
            lastTime = currentTime - (elapsed % fpsInterval);
            update();
            draw();
        }
    }
}

function update() {
    player.updatePlayerPosition();
    camera.follow(player);
    for (let [key,value] of bullets){
        value.move();
    }
    for (let [key,value] of playerBullets){
        value.move();
        value.isCollapsing();
    }
    if (mouseDown) shoot();
}

function draw() {
    ctx.fillStyle = 'rgb(51,51,51)';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.save();
    drawVision();

    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    let rx = (player.x + player.width / 2) - camera.x;
    let ry = (player.y + player.height / 2) - camera.y;

    ctx.strokeStyle = 'black';
    ctx.beginPath();
    ctx.arc(rx, ry, shootRadius, 0, Math.PI * 2);
    ctx.stroke();

    let angle = Math.atan2(mouseY - ry, mouseX - rx);

    let pointX = rx + shootRadius * Math.cos(angle);
    let pointY = ry + shootRadius * Math.sin(angle);

    // Zeichne den Punkt
    ctx.fillStyle = 'red';
    ctx.beginPath();
    ctx.arc(pointX, pointY, 3, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = "rgb(255,0,0)";

    for (let [key, value] of players) {
        ctx.fillRect(value.x - camera.x, value.y - camera.y, value.width, value.height);
    }

    ctx.fillStyle = "rgb(18,116,2)";
    for (let [key, value] of bullets) {
        ctx.beginPath();
        ctx.arc(value.x - camera.x, value.y - camera.y, value.radius, 0, Math.PI * 2);
        ctx.fill();
    }
    for (let [key, value] of playerBullets) {
        ctx.beginPath();
        ctx.arc(value.x - camera.x, value.y - camera.y, value.radius, 0, Math.PI * 2);
        ctx.fill();
    }

    ctx.restore();
    obstacles.forEach(drawObstacle);
}
function drawVision() {
    ctx.strokeStyle = "rgba(0,0,0,0.5)";
    ctx.lineWidth = 1;

    let x1 = (player.x + player.width / 2) - camera.x;
    let y1 = (player.y + player.height / 2) - camera.y;

    let angle = Math.atan2(mouseY - y1, mouseX - x1);

    let angle1 = angle + playerVisionAngle * Math.PI / 180;
    let angle2 = angle - playerVisionAngle * Math.PI / 180;

    let length = 1000;
    let x2_1 = x1 + length * Math.cos(angle1);
    let y2_1 = y1 + length * Math.sin(angle1);
    let x2_2 = x1 + length * Math.cos(angle2);
    let y2_2 = y1 + length * Math.sin(angle2);

    ctx.beginPath();
    ctx.arc(x1, y1, playerVisionRadius, angle1, angle2);
    ctx.lineTo(x1, y1);
    ctx.lineTo(x2_1, y2_1);
    ctx.lineTo(x2_2, y2_2);
    ctx.lineTo(x1, y1);
    ctx.closePath();
    ctx.clip();
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
    let bulX = (player.x + player.width / 2);
    let bulY  = (player.y + player.height / 2);
    const angle = Math.atan2(mouseY - (bulY - camera.y), mouseX - (bulX - camera.x));

    let pointX = bulX + shootRadius * Math.cos(angle);
    let pointY = bulY + shootRadius * Math.sin(angle);

    stompClient.send("/app/game.shoot/" + code,
        {},
        JSON.stringify({type: 'SHOOT', player: player.username,content: pointX + "," + pointY + "," + angle + "," + weapon.speed, code: code})
    );
}
