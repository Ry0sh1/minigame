class Player {
    x;
    y;
    username;
    width;
    height;
    speed;
    dir;
    constructor(x, y, username) {
        this.x = x;
        this.y = y;
        this.username = username;
        this.width = 10;
        this.height = 10;
        this.speed = 1;
        this.dir = 1;
    }
}