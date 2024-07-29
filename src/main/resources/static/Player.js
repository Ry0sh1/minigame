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
        const proposedPosition = { ...this };

        if (keys.w && this.y >0 ) proposedPosition.y -= this.speed;
        if (keys.a && this.x >0 ) proposedPosition.x -= this.speed;
        if (keys.s && this.y < settings.mapHeight -this.height) proposedPosition.y += this.speed;
        if (keys.d && this.x < settings.mapWidth - this.width) proposedPosition.x += this.speed;

        if (!isCollidingWithObstacle(proposedPosition) && (proposedPosition.x !== this.x || proposedPosition.y !== this.y)) {
            stompClient.send("/app/game.position/" + code,
                {},
                JSON.stringify({type: 'POSITION', player: this.username,content: proposedPosition.x+','+proposedPosition.y, code: code})
            );
        }
    }

}