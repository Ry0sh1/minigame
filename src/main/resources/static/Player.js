class Player {
    x;
    y;
    username;
    width;
    height;
    speed;
    constructor(x, y, username) {
        this.x = x;
        this.y = y;
        this.username = username;
        this.width = 10;
        this.height = 10;
        this.speed = 1;
    }

    updatePlayerPosition() {
        const proposedPosition = { ...this };

        if (keys.w && this.y >0 ) proposedPosition.y -= this.speed;
        if (keys.a && this.x >0 ) proposedPosition.x -= this.speed;
        if (keys.s && this.y < mapHeight -this.height) proposedPosition.y += this.speed;
        if (keys.d && this.x < mapWidth - this.width) proposedPosition.x += this.speed;

        if (!isCollidingWithObstacle(proposedPosition) && (proposedPosition.x !== this.x || proposedPosition.y !== this.y)) {
            stompClient.send("/app/game.pos/" + code,
                {},
                JSON.stringify({type: 'POS', player: this.username,content: proposedPosition.x+','+proposedPosition.y, code: code})
            );
        }
    }

}