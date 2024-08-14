function SPAWN_POWERUP(message){
    const content = JSON.parse(message.content);
    let newPowerUp = new PowerUp(parseInt(content.id), content.name, parseInt(content.x), parseInt(content.y));
    powerUps.set(newPowerUp.id, newPowerUp);
}