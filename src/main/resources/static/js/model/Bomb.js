class Bomb{
    p;
    id;
    x;
    y;
    timer;
    radius;
    damage;
    exploded;
    bombAfterLifeTimer;
    constructor(p,id,x,y,ex) {
        this.p = p;
        this.id = id;
        this.x = x;
        this.y = y;
        this.timer = settings.bombTimer;
        this.radius = settings.bombRadius;
        this.damage = settings.bombDamage;
        this.exploded = ex;
        this.bombAfterLifeTimer = settings.bombAfterLifeTimer;
    }

    draw(){
        if (!this.exploded){
            ctx.drawImage(bombImage, this.x - camera.x, this.y - camera.y, bombImage.width, bombImage.height);
        }else {
            ctx.beginPath();
            ctx.fillStyle = "rgba(236,101,101,0.3)"
            ctx.arc(this.x - camera.x, this.y - camera.y, this.radius, 0, Math.PI * 2);
            ctx.fill();
        }
    }

    update(){
        this.timer = this.timer - 1;
        if (this.timer <= 0 && !this.exploded){
            this.explode();
            this.timer = this.bombAfterLifeTimer;
        } else if (this.timer <= 0 && this.exploded){
            this.delete();
        }
    }

    explode(){
        this.exploded = true;
        if (this.p !== username){
            return;
        }
        for (let [key,value] of players){
            if (this.isPlayerInRadius(value)){
                stompClient.send("/app/game.player-hit/" + code,
                    {},
                    JSON.stringify({type: 'PLAYER_HIT', player: player.username, content: value.username + "," + this.damage, code: code})
                );
            }
        }
    }

    isPlayerInRadius(p) {
        const pCenterX = p.x + p.width / 2;
        const pCenterY = p.y + p.height / 2;

        const dx = pCenterX - this.x;
        const dy = pCenterY - this.y;
        const distanz = Math.sqrt(dx * dx + dy * dy);

        return distanz <= this.radius;
    }

    delete(){
        bombs.delete(this.id);
    }

}