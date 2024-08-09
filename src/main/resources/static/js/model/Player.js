class Player {
    angle;
    weapon;
    constructor(x, y, username, angle, weapon) {
        this.x = x;
        this.y = y;
        this.hp = 100;
        this.username = username;
        this.width = settings.playerWidth;
        this.height = settings.playerHeight;
        this.speed = settings.playerSpeed;
        this.nearsight = false;
        this.angle = angle;
        this.weapon = weapon;
    }

    updatePlayerPosition() {
        let proposedPosition = {x: this.x, y: this.y};

        let realSpeed = this.speed;

        if ((keys.w || keys.s) && (keys.a || keys.d)) {
            realSpeed /= Math.sqrt(2);
        }

        if (keys.w && this.y > 0) proposedPosition.y -= realSpeed;
        if (keys.a && this.x > 0) proposedPosition.x -= realSpeed;
        if (keys.s && this.y < map.height - this.height) proposedPosition.y += realSpeed;
        if (keys.d && this.x < map.width - this.width) proposedPosition.x += realSpeed;

        this.isTouchingHeal();

        proposedPosition = this.isCollidingWithObstacle(proposedPosition);

        if (proposedPosition.x !== this.x || proposedPosition.y !== this.y) {
            stompClient.send("/app/game.position/" + code,
                {},
                JSON.stringify({type: 'POSITION', player: this.username, content: proposedPosition.x + ',' + proposedPosition.y, code: code})
            );
        }
    }

    isTouchingHeal(){
        for (let [key, value] of heal){
            const h = value;
            if (h.active){
                if (this.x >= h.x && this.x <= h.x + h.width && this.y >= h.y && this.y <= h.y + h.height ||
                    this.x >= h.x && this.x <= h.x + h.width && this.y + this.height >= h.y && this.y + this.height <= h.y + h.height ||
                    this.x + this.width >= h.x && this.x + this.width <= h.x + h.width && this.y + this.height >= h.y && this.y + this.height <= h.y + h.height ||
                    this.x + this.width >= h.x && this.x + this.width <= h.x + h.width && this.y >= h.y && this.y <= h.y + h.height
                ){
                    stompClient.send("/app/game.heal/" + code,
                        {},
                        JSON.stringify({type: 'HEAL', player: this.username, content: h.id, code: code})
                    );
                }
            }
        }
    }

    isCollidingWithObstacle(pos) {
        if (pos.y < 0) {
            pos.y = 0;
            return pos;
        }
        if (pos.y > map.height) {
            pos.y = map.height;
            return pos;
        }
        if (pos.x < 0) {
            pos.x = 0;
            return pos;
        }
        if (pos.x > map.width) {
            pos.x = map.width;
            return pos;
        }

        map.obstacles.forEach(obstacle => {

            if (pos.x + player.width > obstacle.x && pos.x < obstacle.x &&
                pos.y + player.height > obstacle.y && pos.y < obstacle.y + obstacle.height) {
                pos.x = obstacle.x - player.width;
            }

            if (pos.x < obstacle.x + obstacle.width && pos.x + player.width > obstacle.x + obstacle.width &&
                pos.y + player.height > obstacle.y && pos.y < obstacle.y + obstacle.height) {
                pos.x = obstacle.x + obstacle.width;
            }

            if (pos.y + player.height > obstacle.y && pos.y < obstacle.y &&
                pos.x + player.width > obstacle.x && pos.x < obstacle.x + obstacle.width) {
                pos.y = obstacle.y - player.height;
            }

            if (pos.y < obstacle.y + obstacle.height && pos.y + player.height > obstacle.y + obstacle.height &&
                pos.x + player.width > obstacle.x && pos.x < obstacle.x + obstacle.width) {
                pos.y = obstacle.y + obstacle.height;
            }
        });

        return pos;
    }

}