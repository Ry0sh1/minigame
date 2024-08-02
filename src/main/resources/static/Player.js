class Player {
    x;
    y;
    username;
    width;
    height;
    speed;
    hp;
    constructor(x, y, username) {
        this.x = x;
        this.y = y;
        this.hp = 100;
        this.username = username;
        this.width = settings.playerWidth;
        this.height = settings.playerHeight;
        this.speed = settings.playerSpeed;
    }

    updatePlayerPosition() {
        let proposedPosition = {x: this.x, y: this.y};

        let realSpeed = this.speed;

        if ((keys.w || keys.s) && (keys.a || keys.d)) {
            realSpeed /= Math.sqrt(2);
        }

        if (keys.w && this.y > 0) proposedPosition.y -= realSpeed;
        if (keys.a && this.x > 0) proposedPosition.x -= realSpeed;
        if (keys.s && this.y < settings.mapHeight - this.height) proposedPosition.y += realSpeed;
        if (keys.d && this.x < settings.mapWidth - this.width) proposedPosition.x += realSpeed;

        proposedPosition = this.isCollidingWithObstacle(proposedPosition);

        if (proposedPosition.x !== this.x || proposedPosition.y !== this.y) {
            stompClient.send("/app/game.position/" + code,
                {},
                JSON.stringify({type: 'POSITION', player: this.username, content: proposedPosition.x + ',' + proposedPosition.y, code: code})
            );
            this.x = proposedPosition.x;
            this.y = proposedPosition.y;
        }
    }

    isCollidingWithObstacle(pos) {
        if (pos.y < 0) {
            pos.y = 0;
            return pos;
        }
        if (pos.y > settings.mapHeight) {
            pos.y = settings.mapHeight;
            return pos;
        }
        if (pos.x < 0) {
            pos.x = 0;
            return pos;
        }
        if (pos.x > settings.mapWidth) {
            pos.x = settings.mapWidth;
            return pos;
        }

        obstacles.forEach(obstacle => {

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