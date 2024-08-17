class Player {
    username;
    alive = false;
    width = settings.playerWidth;
    height = settings.playerHeight;
    x;
    y;
    speed = settings.playerSpeed;
    angle = 0;
    weapon;
    nearSight = false;
    killCounter = 0;
    deathCounter = 0;
    currentPowerup;
    shield;
    constructor(username) {
        this.username = username;
        this.shield = 0;
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
        this.isTouchingPowerUp();

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
                        JSON.stringify({type: 'HEAL', player: this.username, content: key, code: code})
                    );
                }
            }
        }
    }
    isTouchingPowerUp(){
        for (let [key, value] of powerUps){
            const p = value;
            if (this.x >= p.x && this.x <= p.x + p.width && this.y >= p.y && this.y <= p.y + p.height ||
                this.x >= p.x && this.x <= p.x + p.width && this.y + this.height >= p.y && this.y + this.height <= p.y + p.height ||
                this.x + this.width >= p.x && this.x + this.width <= p.x + p.width && this.y + this.height >= p.y && this.y + this.height <= p.y + p.height ||
                this.x + this.width >= p.x && this.x + this.width <= p.x + p.width && this.y >= p.y && this.y <= p.y + p.height
            ){

                if(document.getElementById('power-display') != null) document.getElementById('powerup-display').remove();
                let src = "";
                switch (p.name){
                    case "bomb": src = bombImage.src; break;
                    case "laser-gun": src = laserImage.src; break;
                    case "speed": src = speedImage.src; break;
                    case "shield": src = shieldImage.src; break;
                    case "flash": src = flashImage.src; break;
                }
                let template = `<img id="powerup-display" src="${src}" alt="${p.name} Powerup Display">`;
                document.getElementById('powerup-box').insertAdjacentHTML('beforeend', template);
                this.currentPowerup = p.name;
                stompClient.send("/app/game.take-powerup/" + code,
                    {},
                    JSON.stringify({type: 'TAKE_POWERUP', player: this.username, content: key, code: code})
                );
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