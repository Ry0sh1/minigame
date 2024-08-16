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

    }
}
function laserGun(message){
    if (message.player === username){

    }
}
function speed(message){
    if (message.player === username){
        player.speed += 2;
    }
}
function shield(message){
    if (message.player === username){

    }
}