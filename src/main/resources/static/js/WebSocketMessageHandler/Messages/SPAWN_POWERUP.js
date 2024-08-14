function SPAWN_POWERUP(message){
    const content = JSON.parse(message.content);
    let newPowerUp = new PowerUp(content.id, content.name, content.x, content.y);
    powerUps.set(newPowerUp.id, newPowerUp);
}