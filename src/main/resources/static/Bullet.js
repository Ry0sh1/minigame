class Bullet {
    id;
    x;
    y;
    radius;
    speed;
    angle;
    constructor(id,x,y,angle,speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = 3;
        this.speed = speed;
        this.angle = angle;
    }

    move(){
        this.x += Math.cos(this.angle) * this.speed;
        this.y += Math.sin(this.angle) * this.speed;
    }

    isCollapsing(){
        let collapsing = false;
        for (let i = 0; i < obstacles.length; i++){
            const obstacle = obstacles[i];
            if (this.x + this.radius >= obstacle.x &&
                this.x <= obstacle.x + obstacle.width &&
                this.y + this.radius >= obstacle.y &&
                this.y <= obstacle.y + obstacle.height){
                collapsing = true;
                break;
            }
            if (this.x >= mapWidth || this.y >= mapHeight || this.x <= 0 || this.y <= 0){
                collapsing = true;
                break;
            }
        }
        for (let [key, value] of players) {
            const p = value;
            if (this.x + this.radius >= p.x &&
                this.x <= p.x + p.width &&
                this.y + this.radius >= p.y &&
                this.y <= p.y + p.height){
                collapsing = true;
                stompClient.send("/app/game.player-hit/" + code,
                    {},
                    JSON.stringify({type: 'PLAYER_HIT', player: player.username, content: p.username, code: code})
                );
                break;
            }
        }
        if (collapsing){
            stompClient.send("/app/game.delete-bullet/" + code,
                {},
                JSON.stringify({type: 'DELETE_BULLET', player: player.username, content: this.id, code: code})
            );
        }
    }

}



