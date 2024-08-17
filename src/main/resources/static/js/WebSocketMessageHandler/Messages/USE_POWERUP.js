function USE_POWERUP(message) {
    switch (message.content){
        case "bomb": bomb(message); break;
        case "laser-gun": laserGun(message); break;
        case "speed": speed(message); break;
        case "shield": shield(message); break;
    }
}

function bomb(message){
    if (message.player === username){
        player.currentPowerup = null;
    }
    let newBomb=  new Bomb(message.content, players.get(message.player).x, players.get(message.player).y,false);
    bombs.set(message.content, newBomb);
}
function laserGun(message){
    if (message.player === username){
        player.currentPowerup = null;
    }
    let newLaserGun = new LaserGun(message.content, players.get(message.player).x, players.get(message.player).y,players.get(message.player).angle,true)
    laserGuns.set(message.content, newLaserGun);
    console.log(newLaserGun)
}
function speed(message){
    if (message.player === username){
        player.speed += 2;
        player.currentPowerup = null;
    }
}
function shield(message){
    if (message.player === username){
        player.currentPowerup = null;
    }
}