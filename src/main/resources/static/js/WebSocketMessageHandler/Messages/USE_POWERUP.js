function USE_POWERUP(message) {
    switch (message.content){
        case "bomb": bomb(message); break;
        case "laser-gun": laserGun(message); break;
        case "speed": speed(message); break;
        case "shield": shield(message); break;
    }
}

function bomb(message){
    let newBomb=  new Bomb(message.content, players.get(message.player).x, players.get(message.player).y,false);
    bombs.set(message.content, newBomb);
}
function laserGun(message){
    let newLaserGun = new LaserGun(message.content, players.get(message.player).x, players.get(message.player).y,players.get(message.player).angle,true)
    laserGuns.set(message.content, newLaserGun);
}
function speed(message){
    if (message.player === username){
        player.speed += 2;
    }
}
function shield(message){
    if (message.player === username){
        if (player.shield + settings.shieldAmount <= 100){
            player.shield += settings.shieldAmount;
            document.getElementById('shield').innerText = `${player.shield}`;
        }
    }
}