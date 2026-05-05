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

}