const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");

const code = localStorage.getItem("code");
let username = localStorage.getItem("username");
let player;
let weapon;
let camera;
let alive = false;
let currentEvent = null;

let map;

const heal = new Map();

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
const fpsInterval = 1000 / settings.fps;

let reloading = false;
let reloadTime = 0;

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
    if (reloading) {
        reloadTime++;
        if (reloadTime % weapon.reloadFrames === 0){
            reloadTime = 0;
            reloading = false;
        }
    }

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

    ctx.fillStyle = "rgb(255,0,0)";

    for (let [key, value] of players) {
        if (value.username !== username){
            ctx.drawImage(characterImage, value.x - camera.x - characterImage.width / 3, value.y - camera.y - characterImage.height/2, 77/2, 142/2);
        }
    }

    ctx.save();
    ctx.translate(player.x - camera.x + 6, player.y - camera.y + 6);
    let angle = Math.atan2(mouseY - ry, mouseX - rx);
    ctx.rotate(angle + Math.PI / 2);
    ctx.drawImage(characterImage, -12, -30, characterImage.width, characterImage.height);
    ctx.restore();

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
    for (let [key, value] of heal){
        if (value.active){
            ctx.drawImage(healImage, value.x - camera.x, value.y - camera.y, healImage.width, healImage.height);
        }
    }

    ctx.restore();
    map.obstacles.forEach(drawObstacle);
}
function drawObstacle(obstacle) {
    ctx.fillStyle = "rgb(93,120,85)";
    ctx.fillRect(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.width, obstacle.height);

    ctx.strokeStyle = 'black';
    ctx.lineWidth = 2;
    ctx.strokeRect(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.width, obstacle.height);
}

function shoot(){
    if (!reloading){
        let bulX = (player.x + player.width / 2);
        let bulY  = (player.y + player.height / 2);

        if(weapon === shotgun){
            console.log("SHOTGUN")
            const angle = Math.atan2(mouseY - (bulY - camera.y), mouseX - (bulX - camera.x));

            let bulletSpawnX = bulX + settings.shootRadius * Math.cos(angle);
            let bulletSpawnY = bulY + settings.shootRadius * Math.sin(angle);

            for (let i = 0; i <= shotgun.bullets; i++){
                let randomMouseX = mouseX + Math.floor(Math.random() * shotgun.scatter * 2 - shotgun.scatter);
                let randomMouseY = mouseY + Math.floor(Math.random() * shotgun.scatter * 2 - shotgun.scatter);
                const bulAngle = Math.atan2(randomMouseY - (bulY - camera.y), randomMouseX - (bulX - camera.x));

                stompClient.send("/app/game.shoot/" + code,
                    {},
                    JSON.stringify({type: 'SHOOT', player: player.username,content: bulletSpawnX + "," + bulletSpawnY + "," + bulAngle + "," + weapon.speed, code: code})
                );
            }

        }else {
            const angle = Math.atan2(mouseY - (bulY - camera.y), mouseX - (bulX - camera.x));

            let pointX = bulX + settings.shootRadius * Math.cos(angle);
            let pointY = bulY + settings.shootRadius * Math.sin(angle);
            stompClient.send("/app/game.shoot/" + code,
                {},
                JSON.stringify({type: 'SHOOT', player: player.username,content: pointX + "," + pointY + "," + angle + "," + weapon.speed, code: code})
            );
        }
        reloading = true;
    }
}
