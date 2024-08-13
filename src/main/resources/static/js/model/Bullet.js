class Bullet {
    constructor(id,x,y,angle,speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = settings.bulletRadius;
        this.speed = speed;
        this.angle = angle;
        this.distance = 0;
    }

    move(){
        let xTemp = this.x;
        let yTemp = this.y;
        this.x += Math.cos(this.angle) * this.speed;
        this.y += Math.sin(this.angle) * this.speed;
        this.distance += Math.abs(this.x - xTemp) + Math.abs(this.y - yTemp);
    }

    isCollapsing(){
        if (this.distance >= player.weapon.range){
            if (playerBullets.has(this.id)) playerBullets.delete(this.id)
            this.deleteBullet(this.id)
            return;
        }
        if (this.isCollapsingWithObstacle()) this.deleteBullet(this.id);
        let p = this.isCollapsingWithPlayer();
        if (p != null){
            stompClient.send("/app/game.player-hit/" + code,
                {},
                JSON.stringify({type: 'PLAYER_HIT', player: player.username, content: p.username + "," + player.weapon.damage, code: code})
            );
            this.deleteBullet(this.id);
        }
    }

    deleteBullet(id){
        playerBullets.delete(id);
        stompClient.send("/app/game.delete-bullet/" + code,
            {},
            JSON.stringify({type: 'DELETE_BULLET', player: player.username, content: id, code: code})
        );
    }

    isCollapsingWithPlayer(){
        for (let [key, value] of players) {
            if (value.alive){
                const p = value;
                if (this.x + this.radius >= p.x &&
                    this.x <= p.x + p.width &&
                    this.y + this.radius >= p.y &&
                    this.y <= p.y + p.height){
                    return p;
                }
            }
        }
        return null;
    }

    isCollapsingWithObstacle(){
        for (let i = 0; i < map.obstacles.length; i++){
            const obstacle = map.obstacles[i];
            if (this.x + this.radius >= obstacle.x &&
                this.x <= obstacle.x + obstacle.width &&
                this.y + this.radius >= obstacle.y &&
                this.y <= obstacle.y + obstacle.height){
                return true;
            }
            if (this.x >= map.width || this.y >= map.height || this.x <= 0 || this.y <= 0){
                return true;
            }
        }
        return false;
    }

}



