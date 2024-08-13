const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");

const code = localStorage.getItem("code");
let username = localStorage.getItem("username");
let player;
let camera;
let currentEvent = null;
let currentDeathTimer = 0;
let firstSpawn = true;

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
    if (!firstSpawn && player.alive){
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
        if (reloadTime % player.weapon.reloadFrames === 0){
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
        if (player.weapon !== shotgun){
            value.isCollapsing();
        }
    }
    if (player.weapon === shotgun){
       shotgunCollapsing();
    }
    if (mouseDown) shoot();
}

let temp = [];
function shotgunCollapsing(){
    for (let [key, value] of playerBullets) {
        let shotPlayer = value.isCollapsingWithPlayer();
        if (shotPlayer != null){
            temp.push(shotPlayer.username);
            playerBullets.delete(key);
            value.deleteBullet(key);
        }else if (value.isCollapsingWithObstacle() || value.distance >= player.weapon.range){
            temp.push(null);
            playerBullets.delete(key);
            value.deleteBullet(key);
        }
    }
    if (temp.length >= shotgun.bullets && playerBullets.size === 0){
        stompClient.send("/app/game.shotgun-shot/" + code,
            {},
            JSON.stringify({type: 'SHOTGUN_SHOT', player: player.username, content: temp.join(), code: code})
        );
        temp = [];
    }
}

//TODO Zum Server hinzufügen! Dadurch, dass der Angle jetzt auch dem Server übergeben wird.
function shoot(){
    if (!reloading){
        let bulX = (player.x + player.width / 2);
        let bulY  = (player.y + player.height / 2);

        let bulletSpawnX = bulX + settings.shootRadius * Math.cos(player.angle);
        let bulletSpawnY = bulY + settings.shootRadius * Math.sin(player.angle);

        if(player.weapon === shotgun){
            for (let i = 0; i <= shotgun.bullets; i++){
                const randomOffset = Math.random() * shotgun.scatter - (shotgun.scatter / 2);
                const bulAngle = player.angle + randomOffset;

                stompClient.send("/app/game.shoot/" + code,
                    {},
                    JSON.stringify({type: 'SHOOT', player: player.username,content: bulletSpawnX + "," + bulletSpawnY + "," + bulAngle + "," + player.weapon.speed, code: code})
                );
            }
        }else {
            stompClient.send("/app/game.shoot/" + code,
                {},
                JSON.stringify({type: 'SHOOT', player: player.username,content: bulletSpawnX + "," + bulletSpawnY + "," + player.angle + "," + player.weapon.speed, code: code})
            );
        }
        reloading = true;
    }
}

function getWeaponFromString(weaponString){
    switch (weaponString){
        case 'shotgun': return shotgun;
        case 'sniper': return sniper;
        case 'rifle': return rifle;
    }
}
fetch("/get-map-data/" + code, {method: 'GET'})
    .then(response => response.json())
    .then(data => {
        map = data;
        for (let i = 0; i < map.heal_pads.length; i++){
            heal.set(map.heal_pads[i].id,new Heal(map.heal_pads[i].id,map.heal_pads[i].x,map.heal_pads[i].y,settings.healHitBoxWidth,settings.healHitBoxHeight));
        }
        connect();
        requestAnimationFrame(gameLoop)
    })
