class PowerUp {
    id;
    name;
    x;
    y;
    width = settings.powerUpHitBoxWidth;
    height = settings.powerUpHitBoxHeight;
    constructor(id,name,x,y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    drawPowerup(ctx) {
        if (this.name === "laser-gun"){
            ctx.drawImage(laserImage, this.x - camera.x, this.y - camera.y, laserImage.width, laserImage.height);
        } else if(this.name === "shield"){
            ctx.drawImage(shieldImage, this.x - camera.x, this.y - camera.y, shieldImage.width, shieldImage.height);
        } else if(this.name === "bomb"){
            ctx.drawImage(bombImage, this.x - camera.x, this.y - camera.y, bombImage.width, bombImage.height);
        } else if(this.name === "speed"){
            ctx.drawImage(speedImage, this.x - camera.x, this.y - camera.y, speedImage.width, speedImage.height);
        } else{
            //No texture
            ctx.fillStyle = "rgb(255,0,0)";
            ctx.fillRect(this.x - camera.x, this.y - camera.y, laserImage.width, laserImage.height);
        }
    }
}