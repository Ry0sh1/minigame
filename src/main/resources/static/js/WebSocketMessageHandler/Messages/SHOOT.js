function SHOOT(message){
    let bulletAttributes = (JSON.parse(message.content));
    if (message.player === player.username){
        playerBullets.set(bulletAttributes.id, new Bullet(bulletAttributes.id,bulletAttributes.x,bulletAttributes.y,bulletAttributes.angle,bulletAttributes.speed))
    }
    else{
        bullets.set(bulletAttributes.id, new Bullet(bulletAttributes.id,bulletAttributes.x,bulletAttributes.y,bulletAttributes.angle,bulletAttributes.speed))
    }
}